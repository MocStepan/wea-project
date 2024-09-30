package cz.tul.backend.shared.serviceresult

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = ServiceResult.Success::class, name = "Success"),
  JsonSubTypes.Type(value = ServiceResult.Error::class, name = "Error")
)
sealed class ServiceResult<T, out K : ServiceError> {
  data class Success<T>(val data: T) : ServiceResult<T, Nothing>()

  data class Error<T, K : ServiceError>(val error: K) : ServiceResult<T, K>()
}

@OptIn(ExperimentalContracts::class)
fun ServiceResult<*, *>.isSuccess(): Boolean {
  contract {
    returns(true) implies (this@isSuccess is ServiceResult.Success<*>)
    returns(false) implies (this@isSuccess is ServiceResult.Error<*, *>)
  }
  return this is ServiceResult.Success
}

@OptIn(ExperimentalContracts::class)
fun ServiceResult<*, *>.isError(): Boolean {
  contract {
    returns(true) implies (this@isError is ServiceResult.Error<*, *>)
    returns(false) implies (this@isError is ServiceResult.Success<*>)
  }
  return this is ServiceResult.Error
}
