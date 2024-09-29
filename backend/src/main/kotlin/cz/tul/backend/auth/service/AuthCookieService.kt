package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.cookie.access.AccessTokenService
import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenService
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.entity.RefreshToken
import cz.tul.backend.auth.repository.RefreshTokenRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service
class AuthCookieService(
  private val accessTokenService: AccessTokenService,
  private val refreshTokenService: RefreshTokenService,
  private val refreshTokenRepository: RefreshTokenRepository
) {

  fun login(authUser: AuthUser, response: HttpServletResponse): Boolean {
    val claims = accessTokenService.createClaims(authUser)
    val cookie = accessTokenService.createCookie(claims)
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    assignRefreshToken(authUser, response)
    return true
  }

  private fun assignRefreshToken(authUser: AuthUser, response: HttpServletResponse) {
    val refreshToken = refreshTokenRepository.save(RefreshToken.from(authUser))
    val claims = refreshTokenService.createClaims(refreshToken)
    val cookie = refreshTokenService.createCookie(claims)
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
  }

  fun logout(response: HttpServletResponse) {
    val accessCookie = accessTokenService.clearCookie()
    val refreshCookie = refreshTokenService.clearCookie()
    response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
  }
}
