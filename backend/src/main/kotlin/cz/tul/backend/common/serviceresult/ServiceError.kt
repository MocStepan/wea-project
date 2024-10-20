package cz.tul.backend.common.serviceresult

/**
 * Represents an error that occurred during the service execution and is used for [ServiceResult] error state.
 */
interface ServiceError {
  val name: String
  val message: String
}
