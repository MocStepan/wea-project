package cz.tul.backend.auth.service

import cz.tul.backend.auth.valueobject.Hashed
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

/**
 * Custom password encoder for the application with [BCryptPasswordEncoder].
 * Also uses custom type [Hashed] as a parameter type.
 */
@Component
class AuthPasswordEncoder(
  private val encoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
) {

  /**
   * Encodes the raw password using BCrypt algorithm.
   *
   * @param rawPassword the raw password to encode
   * @return [Hashed] the hashed password
   * @see Hashed
   */
  fun encode(rawPassword: String): Hashed = Hashed(encoder.encode(rawPassword))

  /**
   * Matches the raw password with the hashed password.
   *
   * @param rawPassword the raw password to match
   * @param hashedPassword the hashed password to match
   * @return true if the raw password matches the hashed password, false otherwise
   * @see Hashed
   */
  fun matches(rawPassword: String, hashedPassword: Hashed): Boolean {
    return encoder.matches(rawPassword, hashedPassword.value)
  }
}
