package cz.tul.backend.auth.base.cookie.access

import cz.tul.backend.auth.base.dto.AuthJwtClaims
import cz.tul.backend.auth.base.valueobject.AuthUserRole
import cz.tul.backend.auth.base.valueobject.EmailAddress
import cz.tul.backend.auth.entity.AuthUser

data class AccessTokenClaims(
  override val authUserId: Long,
  override val authUserRole: AuthUserRole,
  override val email: EmailAddress,
) : AuthJwtClaims {

  constructor(authUser: AuthUser) : this(
    authUser.id,
    authUser.role,
    authUser.email
  )

  override fun getName(): String {
    return email.value
  }
}
