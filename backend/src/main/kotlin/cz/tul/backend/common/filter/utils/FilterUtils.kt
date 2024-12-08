package cz.tul.backend.common.filter.utils

import com.fasterxml.jackson.databind.ObjectMapper

fun <T> convertValue(value: Any?, objectMapper: ObjectMapper, clazz: Class<T>): Any? {
  if (value == null) {
    return null
  }
  return objectMapper.convertValue(value, clazz)!!
}
