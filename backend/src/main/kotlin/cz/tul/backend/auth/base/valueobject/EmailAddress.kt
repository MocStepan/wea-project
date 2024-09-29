package cz.tul.backend.auth.base.valueobject

@JvmInline
value class EmailAddress(val value: String) {
  companion object {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
  }

  override fun toString(): String {
    return value
  }

  fun isValid(): Boolean {
    return value.matches(EMAIL_REGEX)
  }
}
