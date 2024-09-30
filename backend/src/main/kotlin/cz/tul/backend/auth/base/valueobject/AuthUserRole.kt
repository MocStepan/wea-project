package cz.tul.backend.auth.base.valueobject

import org.springframework.security.core.GrantedAuthority

enum class AuthUserRole : GrantedAuthority {
  USER;

  override fun getAuthority(): String {
    return name
  }
}
