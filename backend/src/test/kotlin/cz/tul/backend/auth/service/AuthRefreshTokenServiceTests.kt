package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenClaims
import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenJwtService
import cz.tul.backend.auth.entity.RefreshToken
import cz.tul.backend.auth.repository.RefreshTokenRepository
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createResponseCookie
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.repository.findByIdOrNull

class AuthRefreshTokenServiceTests : FeatureSpec({

  feature("authenticate") {
    scenario("success") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()

      val authUser = createAuthUser()
      val refreshToken = RefreshToken.from(authUser)
      val refreshClaims = RefreshTokenClaims(refreshToken)

      val cookieName = "refresh_token"
      val cookieValue = "value"
      val responseCookie = createResponseCookie(cookieName = cookieName, value = cookieValue)
      val cookie = Cookie(cookieName, cookieValue)

      val refreshTokenSlot = slot<RefreshToken>()

      every { spec.refreshTokenJwtService.cookieName } returns cookieName
      every { request.cookies } returns arrayOf(cookie)
      every { spec.refreshTokenJwtService.extractClaims(cookieValue) } returns refreshClaims
      every { spec.refreshTokenRepository.findByIdOrNull(0L) } returns refreshToken
      every { spec.refreshTokenRepository.findByAuthUser_Id(0L) } returns setOf(refreshToken)
      every { spec.refreshTokenRepository.deleteAll(setOf(refreshToken)) } just runs
      every { spec.refreshTokenRepository.save(capture(refreshTokenSlot)) } answers { firstArg() }
      every { spec.refreshTokenJwtService.createClaims(any()) } returns refreshClaims
      every { spec.refreshTokenJwtService.createCookie(refreshClaims) } returns responseCookie

      val result = spec.authRefreshTokenService.authenticate(request)!!

      result.first shouldBe authUser
      result.second shouldBe responseCookie
      refreshTokenSlot.captured.authUser shouldBe authUser
      verify(exactly = 1) { spec.refreshTokenRepository.save(any()) }
    }

    scenario("no refresh token was extracted") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()

      val cookieName = "refresh_token"
      val cookieValue = "value"
      val cookie = Cookie(cookieName, cookieValue)

      every { spec.refreshTokenJwtService.cookieName } returns cookieName
      every { request.cookies } returns arrayOf(cookie)
      every { spec.refreshTokenJwtService.extractClaims(cookieValue) } returns null

      val result = spec.authRefreshTokenService.authenticate(request)

      result shouldBe null
      verify(exactly = 0) { spec.refreshTokenRepository.save(any()) }
      verify(exactly = 0) { spec.refreshTokenRepository.findByIdOrNull(any()) }
    }

    scenario("no refresh token was found in the request") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()

      val cookieName = "refresh_token"

      every { spec.refreshTokenJwtService.cookieName } returns cookieName
      every { request.cookies } returns arrayOf()

      val result = spec.authRefreshTokenService.authenticate(request)

      result shouldBe null
      verify(exactly = 0) { spec.refreshTokenJwtService.extractClaims(any()) }
    }
  }

  feature("assignRefreshToken") {
    scenario("success") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val refreshToken = RefreshToken.from(authUser)
      val refreshClaims = RefreshTokenClaims(refreshToken)

      val cookieName = "refresh_token"
      val responseCookie = createResponseCookie(cookieName = cookieName, value = "value")

      val refreshTokenSlot = slot<RefreshToken>()

      every { spec.refreshTokenRepository.findByAuthUser_Id(0L) } returns setOf(refreshToken)
      every { spec.refreshTokenRepository.deleteAll(setOf(refreshToken)) } just runs
      every { spec.refreshTokenRepository.save(capture(refreshTokenSlot)) } answers { refreshToken }
      every { spec.refreshTokenJwtService.createClaims(refreshToken) } returns refreshClaims
      every { spec.refreshTokenJwtService.createCookie(refreshClaims) } returns responseCookie

      val result = spec.authRefreshTokenService.assignRefreshToken(authUser)

      result shouldBe responseCookie
      refreshTokenSlot.captured.authUser shouldBe authUser
      verify(exactly = 1) { spec.refreshTokenRepository.save(any()) }
    }

    scenario("no refresh token was deleted") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val refreshToken = RefreshToken.from(authUser)
      val refreshClaims = RefreshTokenClaims(refreshToken)

      val cookieName = "refresh_token"
      val responseCookie = createResponseCookie(cookieName = cookieName, value = "value")

      val refreshTokenSlot = slot<RefreshToken>()

      every { spec.refreshTokenRepository.findByAuthUser_Id(0L) } returns emptySet()
      every { spec.refreshTokenRepository.save(capture(refreshTokenSlot)) } answers { refreshToken }
      every { spec.refreshTokenJwtService.createClaims(refreshToken) } returns refreshClaims
      every { spec.refreshTokenJwtService.createCookie(refreshClaims) } returns responseCookie

      val result = spec.authRefreshTokenService.assignRefreshToken(authUser)

      result shouldBe responseCookie
      refreshTokenSlot.captured.authUser shouldBe authUser
      verify(exactly = 0) { spec.refreshTokenRepository.deleteAll(any()) }
    }
  }

  feature("clearCookies") {
    scenario("success") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()

      val authUser = createAuthUser()
      val refreshToken = RefreshToken.from(authUser)
      val refreshClaims = RefreshTokenClaims(refreshToken)

      val cookieName = "refresh_token"
      val cookieValue = "value"
      val responseCookie = createResponseCookie(cookieName = cookieName, value = cookieValue)
      val cookie = Cookie(cookieName, cookieValue)

      every { spec.refreshTokenJwtService.cookieName } returns cookieName
      every { request.cookies } returns arrayOf(cookie)
      every { spec.refreshTokenJwtService.extractClaims(cookieValue) } returns refreshClaims
      every { spec.refreshTokenRepository.findByIdOrNull(0L) } returns refreshToken
      every { spec.refreshTokenRepository.delete(refreshToken) } just runs
      every { spec.refreshTokenJwtService.createEmptyCookie() } returns responseCookie

      val result = spec.authRefreshTokenService.clearCookies(request)

      result shouldBe responseCookie
      verify(exactly = 1) { spec.refreshTokenRepository.delete(refreshToken) }
    }

    scenario("no refresh token was deleted") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()

      val cookieName = "refresh_token"
      val cookieValue = "value"
      val responseCookie = createResponseCookie(cookieName = cookieName, value = cookieValue)

      every { spec.refreshTokenJwtService.cookieName } returns cookieName
      every { request.cookies } returns emptyArray()
      every { spec.refreshTokenJwtService.createEmptyCookie() } returns responseCookie

      val result = spec.authRefreshTokenService.clearCookies(request)

      result shouldBe responseCookie
      verify(exactly = 0) { spec.refreshTokenRepository.delete(any()) }
    }
  }
})

private class AuthRefreshTokenServiceSpecWrapper(
  val refreshTokenRepository: RefreshTokenRepository,
  val refreshTokenJwtService: RefreshTokenJwtService
) {
  val authRefreshTokenService: AuthRefreshTokenService = AuthRefreshTokenService(
    refreshTokenRepository,
    refreshTokenJwtService
  )
}

private fun getSpec() = AuthRefreshTokenServiceSpecWrapper(mockk(), mockk())
