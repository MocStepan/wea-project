package cz.tul.backend.auth.base.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter jwt token from the request once per request.
 */
@Service
class JwtTokenFilterService(
  private val tokenFilterComponent: TokenFilterComponent
) : OncePerRequestFilter() {

  /**
   * Filter the access token from the request. If the access token is valid, set the authentication context.
   *
   * @param request The HTTP request
   * @param response The HTTP response
   * @param filterChain The filter chain
   */
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    val validClaims = tokenFilterComponent.filter(request, response)

    if (validClaims != null) {
      val authToken = UsernamePasswordAuthenticationToken(validClaims, null, listOf(validClaims.authRole))
      SecurityContextHolder.getContext().authentication = authToken
    }

    filterChain.doFilter(request, response)
  }
}
