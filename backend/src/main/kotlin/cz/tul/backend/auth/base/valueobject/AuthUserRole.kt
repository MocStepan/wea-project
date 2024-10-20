package cz.tul.backend.auth.base.valueobject

import org.springframework.security.core.GrantedAuthority

/**
 * Enum for authorization roles extending [GrantedAuthority] used for Spring Security.
 */
enum class AuthUserRole : GrantedAuthority {
  USER,
  ADMIN;

  /**
   * Returns the name of the role.
   */
  override fun getAuthority(): String {
    return name
  }
}
