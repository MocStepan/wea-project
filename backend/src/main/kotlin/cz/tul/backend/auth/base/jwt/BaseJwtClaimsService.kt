package cz.tul.backend.auth.base.jwt

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.auth.base.dto.JwtClaims
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Duration

private val log = KotlinLogging.logger { }

open class BaseJwtClaimsService<T>(
  private val objectMapper: ObjectMapper,
  private val claimType: Class<T>,
  secret: String
) : JwtClaimsService<T> {
  private val jwtService = JwtService(secret)

  private fun claimsToMap(claims: T): Map<String, *> {
    return objectMapper.convertValue(claims, object : TypeReference<Map<String, *>>() {})
  }

  private fun Map<String, *>.convertToEntity(): T? {
    return try {
      objectMapper.convertValue(this, claimType)
    } catch (e: Exception) {
      log.error(e) { "Failed to convert map to claims: $this" }
      null
    }
  }

  override fun extractClaims(token: String): T? {
    return jwtService.extractToken(token)?.convertToEntity()
  }

  override fun createToken(
    claims: T,
    duration: Duration
  ): String {
    return claimsToMap(claims).let {
      jwtService.generateToken(JwtClaims.from(it, duration))
    }
  }
}
