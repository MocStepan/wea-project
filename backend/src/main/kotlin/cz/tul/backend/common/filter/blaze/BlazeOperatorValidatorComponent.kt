package cz.tul.backend.common.filter.blaze

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class BlazeOperatorValidatorComponent {

  fun castToInOperator(value: Any?): Collection<*>? {
    if (value !is Collection<*>) {
      log.warn { "IN operator value must be a collection" }
      return null
    }
    return value
  }

  fun castToILikeOperator(value: Any?): String? {
    if (value !is String) {
      log.warn { "ILIKE operator value must be a string" }
      return null
    }
    return "%$value%"
  }
}
