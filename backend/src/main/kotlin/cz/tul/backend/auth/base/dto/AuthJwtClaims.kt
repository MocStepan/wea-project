package cz.tul.backend.auth.base.dto

import cz.tul.backend.auth.base.valueobject.AuthUserRole
import cz.tul.backend.auth.base.valueobject.EmailAddress
import org.springframework.security.core.AuthenticatedPrincipal

interface AuthJwtClaims : AuthenticatedPrincipal {
  val authUserId: Long
  val authUserRole: AuthUserRole
  val email: EmailAddress

  fun isRegistered(): Boolean = authUserRole == AuthUserRole.USER
}
