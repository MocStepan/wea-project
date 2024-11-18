package cz.tul.backend.personinfo.dto

import cz.tul.backend.personinfo.entity.PersonInfo
import cz.tul.backend.personinfo.valueobject.AddressType
import cz.tul.backend.personinfo.valueobject.Gender
import java.time.LocalDate

data class PersonInfoDTO(
  val gender: Gender? = null,
  val birthDate: LocalDate? = null,
  val favoriteCategory: String? = null,
  val referenceSource: String? = null,
  val processingConsent: Boolean = false,
  val personalAddress: PersonInfoAddressDTO? = null,
  val billingAddress: PersonInfoAddressDTO? = null
) {

  companion object {
    fun from(personInfo: PersonInfo): PersonInfoDTO {
      return PersonInfoDTO(
        gender = personInfo.gender,
        birthDate = personInfo.birthDate,
        favoriteCategory = personInfo.favoriteCategory,
        referenceSource = personInfo.referenceSource,
        processingConsent = personInfo.processingConsent,
        personalAddress = personInfo.personInfoAddress.find { it.addressType == AddressType.PERSONAL }?.let {
          PersonInfoAddressDTO.from(it)
        },
        billingAddress = personInfo.personInfoAddress.find { it.addressType == AddressType.BILLING }?.let {
          PersonInfoAddressDTO.from(it)
        }
      )
    }
  }
}
