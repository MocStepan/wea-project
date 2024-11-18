package cz.tul.backend.auth.base.api

import cz.tul.backend.auth.base.valueobject.AuthRole
import org.springframework.security.core.AuthenticatedPrincipal

/**
 * Interface for JWT claims that are used in the application.
 * It extends [AuthenticatedPrincipal] to be able to use it in Spring Security.
 */
interface AuthJwtClaims : AuthenticatedPrincipal {
  val authUserId: Long
  val authRole: AuthRole
}
