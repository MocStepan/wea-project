package cz.tul.backend.auth.valueobject

import cz.tul.backend.common.serviceresult.ServiceError

enum class AuthPasswordServiceRegisterError(override val message: String) : ServiceError {
  INVALID_DATA("Invalid data"),
  USER_ALREADY_EXISTS("User already exists")
}
