package cz.tul.backend.auth.base.cookie.access

import cz.tul.backend.auth.base.jwt.JwtClaimsCreatorService
import cz.tul.backend.auth.base.jwt.JwtClaimsService
import cz.tul.backend.auth.base.jwt.JwtCookieService
import cz.tul.backend.auth.entity.AuthUser

/**
 * Service for creating and managing [AccessTokenClaims].
 */
class AccessTokenJwtService(
  claimsService: JwtClaimsService<AccessTokenClaims>,
  cookieService: JwtCookieService<AccessTokenClaims>
) : JwtClaimsCreatorService<AuthUser, AccessTokenClaims>,
  JwtClaimsService<AccessTokenClaims> by claimsService,
  JwtCookieService<AccessTokenClaims> by cookieService {

  /**
   * Creates [AccessTokenClaims] from [AuthUser].
   *
   * @param value User to create claims for.
   * @return Access token claims.
   */
  override fun createClaims(value: AuthUser): AccessTokenClaims {
    return AccessTokenClaims(value)
  }
}
