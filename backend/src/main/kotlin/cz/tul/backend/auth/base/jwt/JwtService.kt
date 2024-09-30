package cz.tul.backend.auth.base.jwt

import cz.tul.backend.auth.base.dto.JwtClaims
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys

class JwtService(
  secret: String
) {
  private val secretKey = Keys.hmacShaKeyFor(secret.toByteArray())

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

  fun extractToken(token: String): Claims? {
    return try {
      return Jwts
        .parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .payload
    } catch (e: Exception) {
      null
    }
  }
}
