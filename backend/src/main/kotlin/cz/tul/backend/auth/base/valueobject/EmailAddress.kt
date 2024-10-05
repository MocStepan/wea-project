package cz.tul.backend.auth.base.valueobject

import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

@JvmInline
value class EmailAddress(val value: String) {
  companion object {
    private val emailRegex = Regex("^[\\w\\W]+@([\\w-]+\\.)+[\\w-]{2,}\$")
  }

  override fun toString(): String {
    return value
  }

  fun isValid(): Boolean {
    return value.matches(emailRegex).also { if (!it) log.warn { "Email address '$value' is invalid" } }
  }
}
