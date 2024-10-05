package cz.tul.backend.auth.dto

import cz.tul.backend.auth.base.valueobject.EmailAddress

data class AuthLoginDTO(
  val email: EmailAddress,
  val password: String,
  val rememberMe: Boolean = false
) {
  fun isValid(): Boolean {
    return email.isValid() && password.isNotBlank()
  }
}
