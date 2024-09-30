package cz.tul.backend.auth.base.dto

import cz.tul.backend.auth.base.valueobject.AuthUserRole
import org.springframework.security.core.AuthenticatedPrincipal

interface AuthJwtClaims : AuthenticatedPrincipal {
  val authUserId: Long
  val authUserRole: AuthUserRole

  fun isRegistered(): Boolean = authUserRole == AuthUserRole.USER
}
