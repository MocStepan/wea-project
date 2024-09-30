package cz.tul.backend.auth.base.jwt

import org.springframework.http.ResponseCookie

interface JwtCookieService<T> {
  val cookieName: String

  fun createCookie(claims: T): ResponseCookie

  fun clearCookie(): ResponseCookie
}
