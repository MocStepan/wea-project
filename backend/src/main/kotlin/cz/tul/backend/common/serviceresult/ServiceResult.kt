package cz.tul.backend.common.serviceresult

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Represents a result of a service operation.
 *
 * @param T Type of the data returned in case of success.
 * @param K Type of the error returned in case of failure.
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type"
)
@JsonSubTypes(
  JsonSubTypes.Type(value = ServiceResult.Success::class, name = "Success"),
  JsonSubTypes.Type(value = ServiceResult.Error::class, name = "Error")
)
sealed interface ServiceResult<T, out K : ServiceError> {
  data class Success<T, K : ServiceError>(val data: T) : ServiceResult<T, K>
  data class Error<T, K : ServiceError>(val error: K) : ServiceResult<T, K>
}

/**
 * Checks if the result is a success. Is mainly used for smart casting and to inform the compiler about the type.
 *
 * @return True if the result is a success, false otherwise.
 */
@OptIn(ExperimentalContracts::class)
fun ServiceResult<*, *>.isSuccess(): Boolean {
  contract {
    returns(true) implies (this@isSuccess is ServiceResult.Success<*, *>)
    returns(false) implies (this@isSuccess is ServiceResult.Error<*, *>)
  }
  return this is ServiceResult.Success
}

/**
 * Checks if the result is an error. Is mainly used for smart casting and to inform the compiler about the type.
 *
 * @return True if the result is an error, false otherwise.
 */
@OptIn(ExperimentalContracts::class)
fun <T, K : ServiceError> ServiceResult<T, K>.isError(): Boolean {
  contract {
    returns(true) implies (this@isError is ServiceResult.Error<*, *>)
    returns(false) implies (this@isError is ServiceResult.Success<*, *>)
  }
  return this is ServiceResult.Error
}

/**
 * Calls the specified function [onSuccess] with the encapsulated value if this instance represents [ServiceResult.Success].
 * Calls the specified function [onError] with the encapsulated error if this instance represents [ServiceResult.Error].
 *
 * @param onSuccess The function to call if this instance represents [ServiceResult.Success].
 * @param onError The function to call if this instance represents [ServiceResult.Error].
 * @return The result of the function call.
 */
@OptIn(ExperimentalContracts::class)
fun <T, K : ServiceError, R> ServiceResult<T, K>.fold(
  onSuccess: (T) -> R,
  onError: (K) -> R
): R {
  contract {
    callsInPlace(onSuccess, InvocationKind.EXACTLY_ONCE)
    callsInPlace(onError, InvocationKind.EXACTLY_ONCE)
  }
  return when (this) {
    is ServiceResult.Success -> onSuccess(data)
    is ServiceResult.Error -> onError(error)
  }
}
