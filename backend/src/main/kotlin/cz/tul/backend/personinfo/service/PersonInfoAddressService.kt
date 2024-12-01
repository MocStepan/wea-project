package cz.tul.backend.personinfo.service

import cz.tul.backend.personinfo.dto.PersonInfoAddressDTO
import cz.tul.backend.personinfo.entity.PersonInfo
import cz.tul.backend.personinfo.entity.PersonInfoAddress
import cz.tul.backend.personinfo.repository.PersonInfoAddressRepository
import cz.tul.backend.personinfo.valueobject.AddressType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PersonInfoAddressService(
  private val personInfoAddressRepository: PersonInfoAddressRepository
) {

  fun saveAddress(personAddress: PersonInfoAddressDTO, personInfo: PersonInfo, addressType: AddressType) {
    PersonInfoAddress.from(personAddress, personInfo, addressType).let {
      personInfoAddressRepository.save(it)
    }
  }

  fun updateAddress(personAddress: PersonInfoAddressDTO, personInfo: PersonInfo, addressType: AddressType) {
    val address = personInfoAddressRepository.findByPersonInfo_IdAndAddressType(personInfo.id, addressType)
    if (address == null) {
      saveAddress(personAddress, personInfo, addressType)
    } else {
      address.update(personAddress)
      personInfoAddressRepository.save(address)
    }
  }

  fun getRefernceIfExistsByPersonInfoId(personInfoId: Long): PersonInfoAddress? {
    return personInfoAddressRepository.findByPersonInfo_IdAndAddressType(personInfoId, AddressType.BILLING)
  }
}
