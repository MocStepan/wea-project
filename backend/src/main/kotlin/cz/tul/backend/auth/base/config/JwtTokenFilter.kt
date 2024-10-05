package cz.tul.backend.auth.base.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter

@Service
class JwtTokenFilter(
  private val tokenFilter: TokenFilter
) : OncePerRequestFilter() {

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    val validClaims = tokenFilter.filter(request, response)

    if (validClaims != null) {
      val authToken = UsernamePasswordAuthenticationToken(validClaims, null, listOf(validClaims.authUserRole))
      SecurityContextHolder.getContext().authentication = authToken
    }

    filterChain.doFilter(request, response)
  }
}
