package cz.tul.backend.auth.service

import cz.tul.backend.auth.entity.AuthUser
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

/**
 * Service for handling access and refresh cookies.
 */
@Service
class AuthCookieService(
  private val authAccessTokenService: AuthAccessTokenService,
  private val authRefreshTokenService: AuthRefreshTokenService
) {

  /**
   * Set the access cookies to the response and if rememberMe is true, also set the refresh cookie.
   *
   * @param authUser the user to log in
   * @param response the response to set the cookie to
   * @param rememberMe whether to remember the user
   */
  fun setAccessCookie(
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

  /**
   * Try to log in with the refresh cookie. If successful, set the access and refresh cookies to the response.
   * If not, clear the cookies.
   *
   * @param request the request to get the cookie from
   * @param response the response to set the cookie to
   * @return true if the login was successful, false otherwise
   */
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

  /**
   * Clear the access and refresh cookies.
   *
   * @param request the request to get the cookies from
   * @param response the response to set the cookies to
   */
  fun clearCookies(request: HttpServletRequest, response: HttpServletResponse) {
    val accessCookie = authAccessTokenService.clearCookies()
    val refreshCookie = authRefreshTokenService.clearCookies(request)
    response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
  }
}
