package cz.tul.backend.auth.base.config

import cz.tul.backend.auth.base.cookie.access.AccessTokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils

@Service
class JwtTokenFilter(
  private val accessTokenService: AccessTokenService
) : OncePerRequestFilter() {
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    val cookie = WebUtils.getCookie(request, accessTokenService.cookieName)

    if (cookie != null) {
      val accessClaims = accessTokenService.extractClaims(cookie.value)

      if (accessClaims != null) {
        val authentication = UsernamePasswordAuthenticationToken(accessClaims, null, listOf(accessClaims.authUserRole))
        SecurityContextHolder.getContext().authentication = authentication
      }
    }

    filterChain.doFilter(request, response)
  }
}
