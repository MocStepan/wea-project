package cz.tul.backend.auth.base.cookie.refresh

import cz.tul.backend.auth.entity.RefreshToken

data class RefreshTokenClaims(
  val refreshTokenId: Long
) {
  constructor(refreshToken: RefreshToken) : this(
    refreshToken.id
  )
}
