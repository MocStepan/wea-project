package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenJwtService
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.entity.RefreshToken
import cz.tul.backend.auth.repository.RefreshTokenRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.WebUtils

private val log = KotlinLogging.logger { }

@Service
@Transactional
class AuthRefreshTokenService(
  private val refreshTokenRepository: RefreshTokenRepository,
  private val refreshTokenJwtService: RefreshTokenJwtService
) {

  fun authenticate(request: HttpServletRequest): Pair<AuthUser, ResponseCookie>? {
    return getToken(request)?.let { token ->
      val refreshCookie = assignRefreshToken(token.authUser)
      Pair(token.authUser, refreshCookie)
    }
  }

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

  fun clearCookies(request: HttpServletRequest): ResponseCookie {
    val token = getToken(request)
    if (token != null) {
      refreshTokenRepository.delete(token)
    }
    return refreshTokenJwtService.clearCookie()
  }

  private fun getToken(request: HttpServletRequest): RefreshToken? {
    val token = WebUtils.getCookie(request, refreshTokenJwtService.cookieName)?.let { cookie ->
      refreshTokenJwtService.extractClaims(cookie.value)?.let { claims ->
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
