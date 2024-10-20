package cz.tul.backend.auth.valueobject

import cz.tul.backend.common.serviceresult.ServiceError

/**
 * Error types for the password service register method.
 *
 * @param message the error message
 */
enum class AuthPasswordServiceRegisterError(override val message: String) : ServiceError {
  INVALID_DATA("Invalid data"),
  USER_ALREADY_EXISTS("User already exists")
}
