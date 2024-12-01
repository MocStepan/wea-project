package cz.tul.backend.personinfo.service

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.service.AuthUserService
import cz.tul.backend.personinfo.dto.PersonInfoAddressDTO
import cz.tul.backend.personinfo.dto.PersonInfoDTO
import cz.tul.backend.personinfo.entity.PersonInfo
import cz.tul.backend.personinfo.repository.PersonInfoRepository
import cz.tul.backend.personinfo.valueobject.AddressType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class PersonInfoService(
  private val personInfoRepository: PersonInfoRepository,
  private val personInfoAddressService: PersonInfoAddressService,
  private val authUserService: AuthUserService,
  private val personInfoCategoryService: PersonInfoCategoryService
) {

  fun createPersonInfo(personInfoDTO: PersonInfoDTO, claims: AuthJwtClaims): Boolean {
    val authUser = authUserService.getReferenceIfExists(claims.authUserId)
    if (authUser == null) {
      log.warn { "AuthUser not found for user ${claims.authUserId}" }
      return false
    }

    val exists = personInfoRepository.existsByAuthUser_Id(claims.authUserId)
    if (exists) {
      log.warn { "PersonInfo already exists for user ${claims.authUserId}" }
      return false
    }

    val personInfo = PersonInfo.from(personInfoDTO, authUser).let {
      personInfoRepository.save(it)
    }

    personInfoCategoryService.saveCategory(personInfo, personInfoDTO.favoriteCategories)
    personInfoDTO.personalAddress?.saveAddress(personInfo, AddressType.PERSONAL)
    personInfoDTO.billingAddress?.saveAddress(personInfo, AddressType.BILLING)
    return true
  }

  fun getPersonInfo(claims: AuthJwtClaims): PersonInfoDTO? {
    val personInfo = personInfoRepository.findByAuthUser_Id(claims.authUserId)
    if (personInfo == null) {
      log.warn { "PersonInfo not found for user ${claims.authUserId}" }
      return null
    }

    val categories = personInfoCategoryService.getCategory(personInfo.id)
    return PersonInfoDTO.from(personInfo, categories)
  }

  fun updatePersonInfo(personInfoDTO: PersonInfoDTO, claims: AuthJwtClaims): Boolean {
    val personInfo = personInfoRepository.findByAuthUser_Id(claims.authUserId)
    if (personInfo == null) {
      log.warn { "PersonInfo not found for user ${claims.authUserId}" }
      return false
    }

    personInfo.update(personInfoDTO)
    personInfoRepository.save(personInfo)

    personInfoCategoryService.saveCategory(personInfo, personInfoDTO.favoriteCategories)
    personInfoDTO.personalAddress?.updateAddress(personInfo, AddressType.PERSONAL)
    personInfoDTO.billingAddress?.updateAddress(personInfo, AddressType.BILLING)
    return true
  }

  private fun PersonInfoAddressDTO.saveAddress(personInfo: PersonInfo, addressType: AddressType) {
    personInfoAddressService.saveAddress(this, personInfo, addressType)
  }

  private fun PersonInfoAddressDTO.updateAddress(personInfo: PersonInfo, addressType: AddressType) {
    personInfoAddressService.updateAddress(this, personInfo, addressType)
  }

  fun getReferenceIfExistsByAuthUserId(authUserId: Long): PersonInfo? {
    return personInfoRepository.findByAuthUser_Id(authUserId)
  }
}
