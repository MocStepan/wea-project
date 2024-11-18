package cz.tul.backend.personinfo.service

import cz.tul.backend.auth.repository.AuthUserRepository
import cz.tul.backend.personinfo.dto.PersonInfoAddressDTO
import cz.tul.backend.personinfo.dto.PersonInfoDTO
import cz.tul.backend.personinfo.entity.PersonInfo
import cz.tul.backend.personinfo.repository.PersonInfoRepository
import cz.tul.backend.personinfo.valueobject.AddressType
import cz.tul.backend.personinfo.valueobject.Gender
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createPersonInfo
import cz.tul.backend.utils.createPersonInfoAddress
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate

class PersonInfoServiceTests : FeatureSpec({

  feature("create person info") {
    scenario("success with billing address") {
      val spec = getSpec()

      val personInfoAddressDTO = PersonInfoAddressDTO()
      val personInfoDTO = PersonInfoDTO(
        billingAddress = personInfoAddressDTO
      )
      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)

      val personInfoSlot = slot<PersonInfo>()

      every { spec.authUserRepository.findByIdOrNull(claims.authUserId) } returns authUser
      every { spec.personInfoRepository.existsByAuthUser_Id(claims.authUserId) } returns false
      every { spec.personInfoRepository.save(capture(personInfoSlot)) } answers { firstArg() }
      every { spec.personInfoAddressService.saveAddress(personInfoAddressDTO, any(), AddressType.BILLING) } just runs

      val response = spec.personInfoService.createPersonInfo(personInfoDTO, claims)

      response shouldBe true
      personInfoSlot.captured.authUser shouldBe authUser
      personInfoSlot.captured.gender shouldBe null
      personInfoSlot.captured.birthDate shouldBe null
      personInfoSlot.captured.favoriteCategory shouldBe null
      personInfoSlot.captured.referenceSource shouldBe null
      personInfoSlot.captured.processingConsent shouldBe false
    }

    scenario("auth user not found") {
      val spec = getSpec()

      val personInfoDTO = PersonInfoDTO()
      val claims = createUserClaims()

      every { spec.authUserRepository.findByIdOrNull(claims.authUserId) } returns null

      val response = spec.personInfoService.createPersonInfo(personInfoDTO, claims)

      response shouldBe false
    }

    scenario("person info already exists") {
      val spec = getSpec()

      val personInfoDTO = PersonInfoDTO()
      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)

      every { spec.authUserRepository.findByIdOrNull(claims.authUserId) } returns authUser
      every { spec.personInfoRepository.existsByAuthUser_Id(claims.authUserId) } returns true

      val response = spec.personInfoService.createPersonInfo(personInfoDTO, claims)

      response shouldBe false
    }
  }

  feature("get person info") {
    scenario("success") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val personalAddress = createPersonInfoAddress(addressType = AddressType.PERSONAL)
      val billingAddress = createPersonInfoAddress(addressType = AddressType.BILLING)
      val personInfo = createPersonInfo(
        authUser = authUser,
        gender = Gender.MALE,
        birthDate = LocalDate.now(),
        favoriteCategory = "Fantasy",
        referenceSource = "Friend",
        processingConsent = true,
        personInfoAddresses = setOf(personalAddress, billingAddress)
      )
      val claims = createUserClaims(authUser)

      every { spec.personInfoRepository.findByAuthUser_Id(claims.authUserId) } returns personInfo

      val response = spec.personInfoService.getPersonInfo(claims)!!

      response.gender shouldBe personInfo.gender
      response.birthDate shouldBe personInfo.birthDate
      response.favoriteCategory shouldBe personInfo.favoriteCategory
      response.referenceSource shouldBe personInfo.referenceSource
      response.processingConsent shouldBe personInfo.processingConsent

      val address1 = response.personalAddress!!
      address1.country shouldBe personalAddress.country
      address1.city shouldBe personalAddress.city
      address1.street shouldBe personalAddress.street
      address1.houseNumber shouldBe personalAddress.houseNumber
      address1.zipCode shouldBe personalAddress.zipCode

      val address2 = response.billingAddress!!
      address2.country shouldBe billingAddress.country
      address2.city shouldBe billingAddress.city
      address2.street shouldBe billingAddress.street
      address2.houseNumber shouldBe billingAddress.houseNumber
      address2.zipCode shouldBe billingAddress.zipCode
    }

    scenario("person info not found") {
      val spec = getSpec()

      val claims = createUserClaims()

      every { spec.personInfoRepository.findByAuthUser_Id(claims.authUserId) } returns null

      val response = spec.personInfoService.getPersonInfo(claims)

      response shouldBe null
    }
  }

  feature("update person info") {
    scenario("success with personal address") {
      val spec = getSpec()

      val personInfoAddressDTO = PersonInfoAddressDTO()
      val personInfoDTO = PersonInfoDTO(
        personalAddress = personInfoAddressDTO
      )
      val authUser = createAuthUser()
      val personInfo = createPersonInfo(authUser = authUser)
      val claims = createUserClaims(authUser)

      every { spec.personInfoRepository.findByAuthUser_Id(claims.authUserId) } returns personInfo
      every { spec.personInfoRepository.save(personInfo) } returns personInfo
      every {
        spec.personInfoAddressService.updateAddress(
          personInfoAddressDTO,
          personInfo,
          AddressType.PERSONAL
        )
      } just runs

      val response = spec.personInfoService.updatePersonInfo(personInfoDTO, claims)

      response shouldBe true
    }

    scenario("person info not found") {
      val spec = getSpec()

      val personInfoDTO = PersonInfoDTO()
      val claims = createUserClaims()

      every { spec.personInfoRepository.findByAuthUser_Id(claims.authUserId) } returns null

      val response = spec.personInfoService.updatePersonInfo(personInfoDTO, claims)

      response shouldBe false
    }
  }
})

private class PersonInfoServiceSpecWrapper(
  val personInfoRepository: PersonInfoRepository,
  val personInfoAddressService: PersonInfoAddressService,
  val authUserRepository: AuthUserRepository
) {
  val personInfoService = PersonInfoService(personInfoRepository, personInfoAddressService, authUserRepository)
}

private fun getSpec() = PersonInfoServiceSpecWrapper(mockk(), mockk(), mockk())
