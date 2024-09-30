package cz.tul.backend.auth.dto

import cz.tul.backend.auth.base.valueobject.EmailAddress

data class AuthRegisterDTO(
  val firstName: String,
  val lastName: String,
  val email: EmailAddress,
  val password: String,
  val secondPassword: String
) {
  fun isValid(): Boolean {
    return email.isValid() &&
      password.isNotBlank() &&
      firstName.isNotBlank() &&
      lastName.isNotBlank() &&
      password == secondPassword
  }
}
