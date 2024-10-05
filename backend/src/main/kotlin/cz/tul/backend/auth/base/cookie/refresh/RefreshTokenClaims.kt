package cz.tul.backend.auth.base.cookie.refresh

import cz.tul.backend.auth.entity.RefreshToken
import java.time.Instant

data class RefreshTokenClaims(
  val refreshTokenId: Long,
  val exp: Instant? = null
) {
  constructor(refreshToken: RefreshToken) : this(
    refreshToken.id
  )
}
