package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.cookie.access.AccessTokenJwtService
import cz.tul.backend.auth.entity.AuthUser
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthAccessTokenService(
  private val accessTokenJwtService: AccessTokenJwtService
) {
  fun authenticate(authUser: AuthUser): ResponseCookie {
    val claims = accessTokenJwtService.createClaims(authUser)
    return accessTokenJwtService.createCookie(claims)
  }

  fun clearCookies(): ResponseCookie {
    return accessTokenJwtService.clearCookie()
  }
}
