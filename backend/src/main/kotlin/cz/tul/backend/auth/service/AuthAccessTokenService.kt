package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.cookie.access.AccessTokenJwtService
import cz.tul.backend.auth.entity.AuthUser
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service

/**
 * Service for creating and clearing access tokens
 */
@Service
class AuthAccessTokenService(
  private val accessTokenJwtService: AccessTokenJwtService
) {

  /**
   * Create access token from [AuthUser] and set it to the [ResponseCookie]
   *
   * @param authUser user to authenticate
   * @return [ResponseCookie] with access token
   */
  fun authenticate(authUser: AuthUser): ResponseCookie {
    val claims = accessTokenJwtService.createClaims(authUser)
    return accessTokenJwtService.createCookie(claims)
  }

  /**
   * Clear access token cookie and return it
   *
   * @return response cookie with cleared access token
   */
  fun clearCookies(): ResponseCookie {
    return accessTokenJwtService.createEmptyCookie()
  }
}
