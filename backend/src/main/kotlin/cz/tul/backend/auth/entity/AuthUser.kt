package cz.tul.backend.auth.entity

import cz.tul.backend.auth.base.valueobject.AuthRole
import cz.tul.backend.auth.base.valueobject.EmailAddress
import cz.tul.backend.auth.dto.AuthRegisterDTO
import cz.tul.backend.auth.valueobject.Hashed
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity
class AuthUser(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  val firstName: String,
  val lastName: String,
  val email: EmailAddress,
  val password: Hashed,
  @Enumerated(EnumType.STRING)
  val role: AuthRole = AuthRole.USER,
  @OneToMany(mappedBy = "authUser", orphanRemoval = true)
  val refreshToken: Set<RefreshToken> = mutableSetOf(),
  val createdDateTime: LocalDateTime = LocalDateTime.now()
) {
  companion object {
    fun from(
      registerDTO: AuthRegisterDTO,
      password: Hashed
    ): AuthUser {
      return AuthUser(
        firstName = registerDTO.firstName,
        lastName = registerDTO.lastName,
        email = registerDTO.email,
        password = password
      )
    }
  }
}
