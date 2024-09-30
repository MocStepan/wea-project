package cz.tul.backend.auth.base.jwt

import kotlin.time.Duration

interface JwtClaimsService<T> {
  fun extractClaims(token: String): T?

  fun createToken(
    claims: T,
    duration: Duration
  ): String
}
