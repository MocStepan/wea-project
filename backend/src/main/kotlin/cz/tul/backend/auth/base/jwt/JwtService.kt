package cz.tul.backend.auth.base.jwt

import cz.tul.backend.auth.base.api.JwtClaims
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys

private val log = KotlinLogging.logger { }

/**
 * JWT service for generating and extracting JWT tokens.
 */
class JwtService(
  secret: String
) {
  private val secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

  /**
   * Generates JWT token from [JwtClaims].
   *
   * @param claims claims
   * @return JWT token
   */
  fun generateToken(claims: JwtClaims): String {
    return Jwts
      .builder()
      .claims()
      .add(claims.claims)
      .issuedAt(claims.issuedAt)
      .expiration(claims.expiration)
      .and()
      .signWith(secretKey)
      .compact()
  }

  /**
   * Extracts [Claims] from token. If token is invalid or throws an exception, returns null.
   *
   * @param token JWT token
   * @return [Claims] or null
   */
  fun extractToken(token: String): Claims? {
    return try {
      return Jwts
        .parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .payload
    } catch (e: Exception) {
      log.debug { "Failed to extract token" }
      null
    }
  }
}
