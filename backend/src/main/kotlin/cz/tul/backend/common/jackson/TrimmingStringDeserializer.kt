package cz.tul.backend.common.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import org.springframework.boot.jackson.JsonComponent

/**
 * Custom Jackson deserializer that trims the leading and trailing whitespaces from the string.
 */
@JsonComponent
class TrimmingStringDeserializer : StringDeserializer() {
  override fun deserialize(
    p: JsonParser,
    ctxt: DeserializationContext
  ): String {
    val value = p.valueAsString
    return value.trim()
  }
}
