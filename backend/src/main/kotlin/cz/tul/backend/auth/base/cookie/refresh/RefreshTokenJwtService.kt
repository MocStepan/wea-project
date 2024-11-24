package cz.tul.backend.auth.base.cookie.refresh

import cz.tul.backend.auth.base.jwt.JwtClaimsCreatorService
import cz.tul.backend.auth.base.jwt.JwtClaimsService
import cz.tul.backend.auth.base.jwt.JwtCookieService
import cz.tul.backend.auth.entity.RefreshToken

/**
 * Service for creating and managing refresh token claims and cookies.
 */
class RefreshTokenJwtService(
  claimsService: JwtClaimsService<RefreshTokenClaims>,
  cookieService: JwtCookieService<RefreshTokenClaims>
) : JwtClaimsCreatorService<RefreshToken, RefreshTokenClaims>,
  JwtClaimsService<RefreshTokenClaims> by claimsService,
  JwtCookieService<RefreshTokenClaims> by cookieService {

  /**
   * Creates [RefreshTokenClaims] from [RefreshToken].
   *
   * @param value Refresh token.
   * @return Refresh token claims.
   */
  override fun createClaims(value: RefreshToken) = RefreshTokenClaims(value)
}
