package cz.tul.backend.auth.base.cookie.access

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.base.valueobject.AuthRole
import cz.tul.backend.auth.entity.AuthUser

data class AccessTokenClaims(
  override val authUserId: Long,
  override val authRole: AuthRole,
  val email: String
) : AuthJwtClaims {
  constructor(authUser: AuthUser) : this(
    authUser.id,
    authUser.role,
    authUser.email.value
  )

  override fun getName(): String {
    return email
  }
}
