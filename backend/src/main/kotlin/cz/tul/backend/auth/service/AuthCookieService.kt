package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.cookie.utils.addCookie
import cz.tul.backend.auth.entity.AuthUser
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
    response.addCookie(accessCookie)

    if (rememberMe) {
      val refreshCookie = authRefreshTokenService.assignRefreshToken(authUser)
      response.addCookie(refreshCookie)
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
    val authentication = authRefreshTokenService.authenticate(request)
    if (authentication == null) {
      log.warn { "Failed to authenticate with refresh token" }
      clearCookies(request, response)
      return false
    }

    val accessCookie = authAccessTokenService.authenticate(authentication.first)
    response.addCookie(accessCookie)
    response.addCookie(authentication.second)
    return true
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
    response.addCookie(accessCookie)
    response.addCookie(refreshCookie)
  }
}
