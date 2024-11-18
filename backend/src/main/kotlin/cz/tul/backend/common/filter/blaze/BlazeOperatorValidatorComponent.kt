package cz.tul.backend.common.filter.blaze

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

/**
 * Component for validating Blaze-Persistence operators.
 */
@Component
class BlazeOperatorValidatorComponent {

  /**
   * Casts value to IN operator value.
   *
   * @param value value to cast
   * @return collection of values or null
   */
  fun castToInOperator(value: Any?): Collection<*>? {
    if (value !is Collection<*>) {
      log.warn { "IN operator value must be a collection" }
      return null
    }
    return value
  }

  /**
   * Casts value to ILIKE operator value, which is string with % at the beginning and end.
   *
   * @param value value to cast
   * @return value for ILIKE operator or null
   */
  fun castToILikeOperator(value: Any?): String? {
    if (value !is String) {
      log.warn { "ILIKE operator value must be a string" }
      return null
    }
    return "%$value%"
  }

  fun isValidComparable(value: Any?): Boolean {
    val isValid = value is Comparable<*> && value !is String
    if (!isValid) {
      log.warn { "GREATER_THAN/LESS_THAN operators can be used only with Comparable values and not with $value" }
    }
    return isValid
  }
}
