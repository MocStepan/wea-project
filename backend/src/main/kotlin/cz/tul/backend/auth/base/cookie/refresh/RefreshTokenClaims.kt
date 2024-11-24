package cz.tul.backend.auth.base.cookie.refresh

import cz.tul.backend.auth.entity.RefreshToken
import java.time.LocalDateTime

data class RefreshTokenClaims(
  val refreshTokenId: Long,
  val createdDateTime: LocalDateTime
) {
  constructor(refreshToken: RefreshToken) : this(
    refreshToken.id,
    refreshToken.createdDateTime
  )
}
