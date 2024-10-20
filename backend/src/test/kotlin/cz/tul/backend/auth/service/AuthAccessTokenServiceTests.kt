package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.cookie.access.AccessTokenClaims
import cz.tul.backend.auth.base.cookie.access.AccessTokenJwtService
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createResponseCookie
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class AuthAccessTokenServiceTests : FeatureSpec({

  feature("authenticate") {
    scenario("should return access cookie") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val accessClaims = AccessTokenClaims(authUser)
      val responseCookie = createResponseCookie(cookieName = "access")

      every { spec.accessTokenJwtService.createClaims(authUser) } returns accessClaims
      every { spec.accessTokenJwtService.createCookie(accessClaims) } returns responseCookie

      val result = spec.authAccessTokenService.authenticate(authUser)

      result shouldBe responseCookie
    }
  }

  feature("clearCookies") {
    scenario("should return empty access cookie") {
      val spec = getSpec()

      val responseCookie = createResponseCookie(cookieName = "access")

      every { spec.accessTokenJwtService.createEmptyCookie() } returns responseCookie

      val result = spec.authAccessTokenService.clearCookies()

      result shouldBe responseCookie
    }
  }
})

private class AuthAccessTokenServiceSpecWrapper(
  val accessTokenJwtService: AccessTokenJwtService
) {
  val authAccessTokenService: AuthAccessTokenService = AuthAccessTokenService(
    accessTokenJwtService
  )
}

private fun getSpec() = AuthAccessTokenServiceSpecWrapper(mockk())
