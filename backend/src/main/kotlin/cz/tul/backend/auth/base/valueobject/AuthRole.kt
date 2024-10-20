package cz.tul.backend.auth.base.valueobject

import org.springframework.security.core.GrantedAuthority

/**
 * Enum for authorization roles extending [GrantedAuthority] used for Spring Security.
 */
enum class AuthRole : GrantedAuthority {
  USER,
  ADMIN;

  /**
   * Returns the name of the role.
   */
  override fun getAuthority(): String {
    return name
  }

  companion object {
    val allAuthorities = AuthRole.entries.map { it.authority }
  }
}
