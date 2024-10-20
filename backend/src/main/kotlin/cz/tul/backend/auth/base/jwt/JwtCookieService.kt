package cz.tul.backend.auth.base.jwt

import org.springframework.http.ResponseCookie

/**
 * Service for creating JWT cookies.
 */
interface JwtCookieService<T> {

  /**
   * Name of the cookie.
   */
  val cookieName: String

  /**
   * Creates a [ResponseCookie] with the provided claims.
   *
   * @param claims claims to be stored in the cookie
   * @return created cookie
   */
  fun createCookie(claims: T): ResponseCookie

  /**
   * Creates an empty [ResponseCookie].
   *
   * @return empty cookie
   */
  fun createEmptyCookie(): ResponseCookie
}
