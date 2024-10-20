package cz.tul.backend.auth.base.config

import cz.tul.backend.auth.base.cookie.access.AccessTokenClaims
import cz.tul.backend.auth.base.cookie.access.AccessTokenJwtService
import cz.tul.backend.utils.createAuthUser
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class TokenFilterComponentTests : FeatureSpec({

  feature("filter without refresh token") {

    scenario("valid access token") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val accessToken = Cookie("name", "token")
      val claims = AccessTokenClaims(createAuthUser())

      every { spec.accessTokenService.cookieName } returns "name"
      every { request.cookies } returns arrayOf(accessToken)
      every { spec.accessTokenService.extractClaims(accessToken.value) } returns claims

      val result = spec.tokenFilterComponent.filter(request, response)!!

      result shouldBe claims
    }

    scenario("cookie not present") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      every { spec.accessTokenService.cookieName } returns "name"
      every { request.cookies } returns arrayOf()

      val result = spec.tokenFilterComponent.filter(request, response)

      result shouldBe null
    }

    scenario("claims expired") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>(relaxed = true)
      val cookie = Cookie("name", "token")

      every { request.cookies } returns arrayOf(cookie)

      every { spec.accessTokenService.cookieName } returns "name"
      every { spec.accessTokenService.extractClaims("token") } returns null

      val result = spec.tokenFilterComponent.filter(request, response)

      result shouldBe null
    }
  }

  feature("filter with refresh token") {

    scenario("access token not present") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>(relaxed = true)

      every { request.cookies } returns arrayOf()
      every { spec.accessTokenService.cookieName } returns "accessToken"

      val result = spec.tokenFilterComponent.filter(request, response)

      result shouldBe null
    }
  }
})

private class TokenFilterComponentSpecWrapper(
  val accessTokenService: AccessTokenJwtService
) {
  val tokenFilterComponent: TokenFilterComponent = TokenFilterComponent(
    accessTokenService
  )
}

private fun getSpec() = TokenFilterComponentSpecWrapper(mockk())
