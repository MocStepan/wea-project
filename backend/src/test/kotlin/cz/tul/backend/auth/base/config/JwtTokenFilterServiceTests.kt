package cz.tul.backend.auth.base.config

import cz.tul.backend.auth.base.cookie.access.AccessTokenClaims
import cz.tul.backend.utils.createAuthUser
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.platform.commons.util.ReflectionUtils
import org.springframework.security.core.context.SecurityContextHolder

class JwtTokenFilterServiceTests : FeatureSpec({

  feature("AuthJwtFilter") {

    scenario("login successfully") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val filterChain = mockk<FilterChain>()
      val claims = AccessTokenClaims(createAuthUser())
      SecurityContextHolder.getContext().authentication = null

      every { spec.tokenFilterComponent.filter(request, response) } returns claims
      every { filterChain.doFilter(request, response) } just Runs

      spec.doFilterInternal(request, response, filterChain)

      verify { filterChain.doFilter(request, response) }
      SecurityContextHolder.getContext().authentication.principal shouldBe claims
    }

    scenario("login failed") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val filterChain = mockk<FilterChain>()
      SecurityContextHolder.getContext().authentication = null

      every { spec.tokenFilterComponent.filter(request, response) } returns null
      every { filterChain.doFilter(request, response) } just Runs

      spec.doFilterInternal(request, response, filterChain)

      SecurityContextHolder.getContext().authentication shouldBe null
      verify { filterChain.doFilter(request, response) }
    }
  }
})

private val doFilterInternalMethod = ReflectionUtils.findMethod(
  JwtTokenFilterService::class.java,
  "doFilterInternal",
  HttpServletRequest::class.java,
  HttpServletResponse::class.java,
  FilterChain::class.java
).get()

private class JwtTokenFilterServiceSpecWrapper(
  val tokenFilterComponent: TokenFilterComponent
) {
  val jwtTokenFilterService = JwtTokenFilterService(
    tokenFilterComponent
  )

  fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) = ReflectionUtils.invokeMethod(
    doFilterInternalMethod,
    jwtTokenFilterService,
    request,
    response,
    filterChain
  )
}

private fun getSpec() = JwtTokenFilterServiceSpecWrapper(mockk())
