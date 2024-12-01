package cz.tul.backend.personinfo.entity

import cz.tul.backend.personinfo.dto.PersonInfoAddressDTO
import cz.tul.backend.personinfo.valueobject.AddressType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class PersonInfoAddress(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @ManyToOne(optional = false)
  val personInfo: PersonInfo,
  @Enumerated(EnumType.STRING)
  val addressType: AddressType,
  var country: String? = null,
  var city: String? = null,
  var street: String? = null,
  var houseNumber: String? = null,
  var zipCode: String? = null
) {

  companion object {
    fun from(
      personInfoAddressDTO: PersonInfoAddressDTO,
      personInfo: PersonInfo,
      addressType: AddressType
    ): PersonInfoAddress {
      return PersonInfoAddress(
        personInfo = personInfo,
        addressType = addressType,
        country = personInfoAddressDTO.country,
        city = personInfoAddressDTO.city,
        street = personInfoAddressDTO.street,
        houseNumber = personInfoAddressDTO.houseNumber,
        zipCode = personInfoAddressDTO.zipCode
      )
    }
  }

  fun update(personInfoAddressDTO: PersonInfoAddressDTO) {
    this.country = personInfoAddressDTO.country
    this.city = personInfoAddressDTO.city
    this.street = personInfoAddressDTO.street
    this.houseNumber = personInfoAddressDTO.houseNumber
    this.zipCode = personInfoAddressDTO.zipCode
  }

  fun isValidForSubmit(): Boolean {
    return country != null && city != null && street != null && houseNumber != null && zipCode != null
  }
}
