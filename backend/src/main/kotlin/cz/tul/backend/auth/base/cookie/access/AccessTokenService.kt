package cz.tul.backend.auth.base.cookie.access

import cz.tul.backend.auth.base.jwt.JwtClaimsCreatorService
import cz.tul.backend.auth.base.jwt.JwtClaimsService
import cz.tul.backend.auth.base.jwt.JwtCookieService
import cz.tul.backend.auth.entity.AuthUser

class AccessTokenService(
  claimsService: JwtClaimsService<AccessTokenClaims>,
  cookieService: JwtCookieService<AccessTokenClaims>
) : JwtClaimsCreatorService<AuthUser, AccessTokenClaims>,
  JwtClaimsService<AccessTokenClaims> by claimsService,
  JwtCookieService<AccessTokenClaims> by cookieService {
  override fun createClaims(value: AuthUser): AccessTokenClaims {
    return AccessTokenClaims(value)
  }
}
