package cz.tul.backend.auth.base.cookie.refresh

import cz.tul.backend.auth.base.jwt.JwtClaimsCreatorService
import cz.tul.backend.auth.base.jwt.JwtClaimsService
import cz.tul.backend.auth.base.jwt.JwtCookieService
import cz.tul.backend.auth.entity.RefreshToken

class RefreshTokenJwtService(
  claimsService: JwtClaimsService<RefreshTokenClaims>,
  cookieService: JwtCookieService<RefreshTokenClaims>
) : JwtClaimsCreatorService<RefreshToken, RefreshTokenClaims>,
  JwtClaimsService<RefreshTokenClaims> by claimsService,
  JwtCookieService<RefreshTokenClaims> by cookieService {
  override fun createClaims(value: RefreshToken): RefreshTokenClaims {
    return RefreshTokenClaims(value)
  }
}
