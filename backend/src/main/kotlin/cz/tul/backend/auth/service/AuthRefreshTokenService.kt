package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenJwtService
import cz.tul.backend.auth.base.cookie.utils.getCookieValue
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.entity.RefreshToken
import cz.tul.backend.auth.repository.RefreshTokenRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger { }

/**
 * Service for handling refresh token cookies
 */
@Service
@Transactional
class AuthRefreshTokenService(
  private val refreshTokenRepository: RefreshTokenRepository,
  private val refreshTokenJwtService: RefreshTokenJwtService
) {

  /**
   * Try to authenticate with the refresh token cookie. If successful, assign a new refresh token and return it.
   *
   * @param request the request to get the cookie from
   * @return the authenticated user and the new refresh cookie or null if the user is not authenticated
   */
  fun authenticate(request: HttpServletRequest): Pair<AuthUser, ResponseCookie>? {
    return getToken(request)?.let { token ->
      val refreshCookie = assignRefreshToken(token.authUser)
      Pair(token.authUser, refreshCookie)
    }
  }

  /**
   * Assign a new refresh token to the user. If the user already has a refresh token, delete it.
   *
   * @param authUser the user to assign the refresh token to
   * @return [ResponseCookie] with the new refresh token
   */
  fun assignRefreshToken(authUser: AuthUser): ResponseCookie {
    refreshTokenRepository.findByAuthUser_Id(authUser.id).takeIf {
      it.isNotEmpty()
    }?.let {
      refreshTokenRepository.deleteAll(it)
    }

    val token = refreshTokenRepository.save(RefreshToken.from(authUser))
    val claims = refreshTokenJwtService.createClaims(token)
    return refreshTokenJwtService.createCookie(claims)
  }

  /**
   * Clear the refresh token cookie and delete the refresh token from the repository.
   *
   * @param request the request to get the cookie from
   * @return [ResponseCookie] with the cleared refresh token
   */
  fun clearCookies(request: HttpServletRequest): ResponseCookie {
    val token = getToken(request)
    if (token != null) {
      refreshTokenRepository.delete(token)
    }
    return refreshTokenJwtService.createEmptyCookie()
  }

  /**
   * Get the refresh token from the request.
   *
   * @param request the request to get the cookie from
   * @return the refresh token or null if not found
   */
  private fun getToken(request: HttpServletRequest): RefreshToken? {
    val token = request.getCookieValue(refreshTokenJwtService.cookieName)?.let { cookie ->
      refreshTokenJwtService.extractClaims(cookie)?.let { claims ->
        refreshTokenRepository.findByIdOrNull(claims.refreshTokenId)
      }
    }

    if (token == null) {
      log.warn { "No refresh token found" }
      return null
    }

    return token
  }
}
