package cz.tul.backend.auth.base.jwt

import org.springframework.http.ResponseCookie
import kotlin.time.Duration

open class BaseJwtCookieService<T>(
  override val cookieName: String,
  private val duration: Duration,
  private val sameSite: String,
  private val secure: Boolean,
  private val claimsService: JwtClaimsService<T>
) : JwtCookieService<T> {
  override fun createCookie(claims: T): ResponseCookie {
    val jwtToken = claimsService.createToken(claims, duration)
    return ResponseCookie.from(cookieName, jwtToken)
      .httpOnly(true)
      .path("/")
      .secure(secure)
      .sameSite(sameSite)
      .maxAge(duration.inWholeSeconds)
      .build()
  }

  override fun clearCookie(): ResponseCookie {
    return ResponseCookie.from(cookieName, "")
      .httpOnly(true)
      .path("/")
      .secure(secure)
      .sameSite(sameSite)
      .maxAge(0)
      .build()
  }
}
