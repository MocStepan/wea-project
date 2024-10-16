package cz.tul.backend.auth.dto

import cz.tul.backend.auth.entity.AuthUser

data class AuthUserDTO(
  val firstName: String,
  val lastName: String
) {

  companion object {
    fun from(authUser: AuthUser): AuthUserDTO {
      return AuthUserDTO(
        firstName = authUser.firstName,
        lastName = authUser.lastName
      )
    }
  }
}
