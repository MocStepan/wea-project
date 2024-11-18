package cz.tul.backend.auth.base.api

import cz.tul.backend.auth.base.valueobject.AuthRole
import org.springframework.stereotype.Component

@Component
class AuthComponent {

  fun isAdmin(authJwtClaims: AuthJwtClaims): Boolean {
    return authJwtClaims.authRole == AuthRole.ADMIN
  }
}
