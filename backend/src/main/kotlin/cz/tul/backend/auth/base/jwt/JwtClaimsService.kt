package cz.tul.backend.auth.base.jwt

import kotlin.time.Duration

/**
 * Service for extracting and creating JWT claims.
 */
interface JwtClaimsService<T> {

  /**
   * Extracts claims from the provided token.
   *
   * @param token token from which to extract claims
   * @return extracted claims or null if the token is invalid
   */
  fun extractClaims(token: String): T?

  /**
   * Creates a token with the provided claims and duration.
   *
   * @param claims claims to be stored in the token
   * @param duration duration for which the token is valid
   * @return created token
   */
  fun createToken(
    claims: T,
    duration: Duration
  ): String
}
