package cz.tul.backend.auth.service

import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createResponseCookie
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders

class AuthCookieServiceTests : FeatureSpec({

  feature("login with access cookie") {
    scenario("should add access and refresh cookies to response") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val response = mockk<HttpServletResponse>()
      val refreshCookie = createResponseCookie(cookieName = "refresh")
      val accessCookie = createResponseCookie(cookieName = "access")

      every { spec.authAccessTokenService.authenticate(authUser) } returns accessCookie
      every { spec.authRefreshTokenService.assignRefreshToken(authUser) } returns refreshCookie
      every { response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString()) } just runs
      every { response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString()) } just runs

      spec.authCookieService.setAccessCookie(authUser, response, true)
    }

    scenario("should add only access cookie to response") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val response = mockk<HttpServletResponse>()
      val accessCookie = createResponseCookie(cookieName = "access")

      every { spec.authAccessTokenService.authenticate(authUser) } returns accessCookie
      every { response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString()) } just runs

      spec.authCookieService.setAccessCookie(authUser, response, false)

      verify(exactly = 0) { spec.authRefreshTokenService.assignRefreshToken(any()) }
    }
  }

  feature("login with refresh cookie") {
    scenario("successfully authenticate with refresh cookie") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser()
      val refreshCookie = createResponseCookie(cookieName = "refresh")
      val accessCookie = createResponseCookie(cookieName = "access")

      every { spec.authRefreshTokenService.authenticate(request) } returns Pair(authUser, refreshCookie)
      every { spec.authAccessTokenService.authenticate(authUser) } returns accessCookie
      every { response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString()) } just runs
      every { response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString()) } just runs

      val result = spec.authCookieService.loginWithRefreshCookie(request, response)

      result shouldBe true
    }

    scenario("invalid refresh token") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val accessEmptyCookie = createResponseCookie(cookieName = "access", value = "")
      val refreshEmptyCookie = createResponseCookie(cookieName = "refresh", value = "")

      every { spec.authRefreshTokenService.authenticate(request) } returns null
      every { spec.authAccessTokenService.clearCookies() } returns accessEmptyCookie
      every { spec.authRefreshTokenService.clearCookies(request) } returns refreshEmptyCookie
      every { response.addHeader(HttpHeaders.SET_COOKIE, accessEmptyCookie.toString()) } just runs
      every { response.addHeader(HttpHeaders.SET_COOKIE, refreshEmptyCookie.toString()) } just runs

      val result = spec.authCookieService.loginWithRefreshCookie(request, response)

      result shouldBe false
      verify(exactly = 0) { spec.authAccessTokenService.authenticate(any()) }
    }
  }
})

private class AuthCookieServiceSpecWrapper(
  val authAccessTokenService: AuthAccessTokenService,
  val authRefreshTokenService: AuthRefreshTokenService
) {
  val authCookieService = AuthCookieService(
    authAccessTokenService,
    authRefreshTokenService
  )
}

private fun getSpec() = AuthCookieServiceSpecWrapper(mockk(), mockk())
