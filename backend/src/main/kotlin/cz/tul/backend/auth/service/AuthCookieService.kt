package cz.tul.backend.auth.service

import cz.tul.backend.auth.entity.AuthUser
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class AuthCookieService(
  private val authAccessTokenService: AuthAccessTokenService,
  private val authRefreshTokenService: AuthRefreshTokenService
) {
  fun loginWithAccessCookie(
    authUser: AuthUser,
    response: HttpServletResponse,
    rememberMe: Boolean
  ) {
    val accessCookie = authAccessTokenService.authenticate(authUser)
    response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())

    if (rememberMe) {
      val refreshCookie = authRefreshTokenService.assignRefreshToken(authUser)
      response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
    }
  }

  fun loginWithRefreshCookie(
    request: HttpServletRequest,
    response: HttpServletResponse
  ): Boolean {
    return try {
      val (authUser, refreshCookie) = authRefreshTokenService.authenticate(request) ?: throw IllegalArgumentException(
        "Invalid refresh token"
      )

      val accessCookie = authAccessTokenService.authenticate(authUser)
      response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())
      response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
      true
    } catch (e: Exception) {
      log.warn(e) { "Failed to authenticate with refresh token" }
      clearCookies(request, response)
      false
    }
  }

  fun clearCookies(request: HttpServletRequest, response: HttpServletResponse) {
    val accessCookie = authAccessTokenService.clearCookies()
    val refreshCookie = authRefreshTokenService.clearCookies(request)
    response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
  }
}
