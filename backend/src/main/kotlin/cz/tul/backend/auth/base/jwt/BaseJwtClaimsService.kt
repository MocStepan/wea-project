package cz.tul.backend.auth.base.jwt

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.auth.base.dto.JwtClaims
import kotlin.time.Duration

open class BaseJwtClaimsService<T>(
  private val objectMapper: ObjectMapper,
  private val claimType: Class<T>,
  secret: String,
) : JwtClaimsService<T> {

  private val jwtService = JwtService(secret)
  override fun extractClaims(token: String): T? {
    return jwtService.extractToken(token)?.let {
      objectMapper.convertValue(it, claimType)
    }
  }

  override fun createToken(claims: T, duration: Duration): String {
    val claimsMap = objectMapper.convertValue(claims, object : TypeReference<Map<String, *>>() {})
    return jwtService.generateToken(JwtClaims.from(claimsMap, duration))
  }
}
