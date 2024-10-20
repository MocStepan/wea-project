package cz.tul.backend.auth.base.jwt

import org.springframework.http.ResponseCookie
import kotlin.time.Duration

/**
 * Base class for [JwtCookieService] implementations. Provides methods for creating and clearing JWT cookie.
 */
class BaseJwtCookieService<T>(
  override val cookieName: String,
  private val duration: Duration,
  private val sameSite: String,
  private val secure: Boolean,
  private val claimsService: JwtClaimsService<T>
) : JwtCookieService<T> {

  /**
   * Create [ResponseCookie] with JWT token.
   *
   * @param claims claims
   * @return [ResponseCookie] with JWT token
   */
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

  /**
   * Set value of the [ResponseCookie] to empty string and max age to 0.
   *
   * @return empty [ResponseCookie]
   */
  override fun createEmptyCookie(): ResponseCookie {
    return ResponseCookie.from(cookieName, "")
      .httpOnly(true)
      .path("/")
      .secure(secure)
      .sameSite(sameSite)
      .maxAge(0)
      .build()
  }
}
