package cz.tul.backend.personinfo.dto

import cz.tul.backend.personinfo.entity.PersonInfoAddress

data class PersonInfoAddressDTO(
  val country: String? = null,
  val city: String? = null,
  val street: String? = null,
  val houseNumber: String? = null,
  val zipCode: String? = null
) {

  companion object {
    fun from(personInfoAddress: PersonInfoAddress): PersonInfoAddressDTO {
      return PersonInfoAddressDTO(
        country = personInfoAddress.country,
        city = personInfoAddress.city,
        street = personInfoAddress.street,
        houseNumber = personInfoAddress.houseNumber,
        zipCode = personInfoAddress.zipCode
      )
    }
  }
}
