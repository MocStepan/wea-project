package cz.tul.backend.personinfo.entity

import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.cart.entity.Cart
import cz.tul.backend.personinfo.dto.PersonInfoDTO
import cz.tul.backend.personinfo.valueobject.Gender
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class PersonInfo(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @OneToOne(optional = false)
  val authUser: AuthUser,
  @Enumerated(EnumType.STRING)
  var gender: Gender? = null,
  var birthDate: LocalDate? = null,
  var referenceSource: String? = null,
  var processingConsent: Boolean = false,
  @OneToMany(mappedBy = "personInfo", orphanRemoval = true)
  val personInfoAddress: Set<PersonInfoAddress> = mutableSetOf(),
  @OneToMany(mappedBy = "personInfo", orphanRemoval = true)
  val personInfoCategories: Set<PersonInfoCategory> = mutableSetOf(),
  @OneToMany(mappedBy = "personInfo", orphanRemoval = true)
  val cart: List<Cart> = mutableListOf(),

  private var updatedDateTime: LocalDateTime? = null,
  val createdDateTime: LocalDateTime = LocalDateTime.now()
) {

  companion object {
    fun from(personInfoDTO: PersonInfoDTO, authUser: AuthUser): PersonInfo {
      return PersonInfo(
        authUser = authUser,
        gender = personInfoDTO.gender,
        birthDate = personInfoDTO.birthDate,
        referenceSource = personInfoDTO.referenceSource,
        processingConsent = personInfoDTO.processingConsent
      )
    }
  }

  fun update(personInfoDTO: PersonInfoDTO) {
    this.gender = personInfoDTO.gender
    this.birthDate = personInfoDTO.birthDate
    this.referenceSource = personInfoDTO.referenceSource
    this.processingConsent = personInfoDTO.processingConsent
    this.updatedDateTime = LocalDateTime.now()
  }
}
