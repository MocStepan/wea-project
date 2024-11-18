package cz.tul.backend.auth.base.jwt

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.auth.base.api.JwtClaims
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Duration

private val log = KotlinLogging.logger { }

/**
 * Base class for [JwtClaimsService]. Encapsulates common logic for extracting and creating JWT tokens.
 */
class BaseJwtClaimsService<T>(
  private val objectMapper: ObjectMapper,
  private val claimType: Class<T>,
  secret: String
) : JwtClaimsService<T> {
  private val jwtService = JwtService(secret)

  /**
   * Converts claims to map.
   *
   * @param claims claims
   * @return claims as map
   */
  private fun claimsToMap(claims: T): Map<String, *> {
    return objectMapper.convertValue(claims, object : TypeReference<Map<String, *>>() {})
  }

  /**
   * Converts map to claims.
   *
   * @return claims
   */
  private fun Map<String, *>.convertToEntity(): T? {
    return try {
      objectMapper.convertValue(this, claimType)
    } catch (e: Exception) {
      log.error(e) { "Failed to convert map to claims: $this" }
      null
    }
  }

  /**
   * Extracts claims from JWT token and converts them claims.
   *
   * @param token JWT token
   * @return claims or null
   */
  override fun extractClaims(token: String): T? {
    return jwtService.extractToken(token)?.convertToEntity()
  }

  /**
   * Converts claims to map and then creates JWT token.
   *
   * @param claims claims (AccessTokenClaims or RefreshTokenClaims)
   * @param duration duration of token
   * @return JWT token
   */
  override fun createToken(
    claims: T,
    duration: Duration
  ): String {
    return claimsToMap(claims).let {
      jwtService.generateToken(JwtClaims.from(it, duration))
    }
  }
}
