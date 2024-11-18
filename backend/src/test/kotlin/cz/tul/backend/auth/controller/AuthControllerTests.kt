package cz.tul.backend.auth.controller

import cz.tul.backend.auth.dto.AuthLoginDTO
import cz.tul.backend.auth.dto.AuthRegisterDTO
import cz.tul.backend.auth.service.AuthCookieService
import cz.tul.backend.auth.service.AuthPasswordService
import cz.tul.backend.auth.valueobject.AuthPasswordServiceRegisterError
import cz.tul.backend.common.serviceresult.ServiceResult
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication

class AuthControllerTests : FeatureSpec({

  feature("login") {
    scenario("login success") {
      val spec = getSpec()

      val authLoginDTO = mockk<AuthLoginDTO>()
      val response = mockk<HttpServletResponse>()

      every { spec.authPasswordService.login(authLoginDTO, response) } returns true

      val result = spec.authController.login(authLoginDTO, response)

      result.statusCode shouldBe HttpStatus.OK
    }

    scenario("login failure") {
      val spec = getSpec()

      val authLoginDTO = mockk<AuthLoginDTO>()
      val response = mockk<HttpServletResponse>()

      every { spec.authPasswordService.login(authLoginDTO, response) } returns false

      val result = spec.authController.login(authLoginDTO, response)

      result.statusCode shouldBe HttpStatus.BAD_REQUEST
    }
  }

  feature("logout") {
    scenario("logout success") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.authPasswordService.logout(request, response, claims) } just runs

      val result = spec.authController.logout(request, response, authentication)

      result.statusCode shouldBe HttpStatus.OK
    }
  }

  feature("register") {
    scenario("register success") {
      val spec = getSpec()

      val authRegisterDTO = mockk<AuthRegisterDTO>()

      every { spec.authPasswordService.register(authRegisterDTO) } returns ServiceResult.Success(true)

      val result = spec.authController.register(authRegisterDTO)

      result.statusCode shouldBe HttpStatus.OK
    }

    scenario("register invalid data") {
      val spec = getSpec()

      val authRegisterDTO = mockk<AuthRegisterDTO>()

      every { spec.authPasswordService.register(authRegisterDTO) } returns ServiceResult.Error(
        AuthPasswordServiceRegisterError.INVALID_DATA
      )

      val result = spec.authController.register(authRegisterDTO)

      result.statusCode shouldBe HttpStatus.BAD_REQUEST
    }
  }

  feature("invoke refresh token") {
    scenario("invoke refresh token success") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()

      every { spec.authCookieService.loginWithRefreshCookie(request, response) } returns true

      val result = spec.authController.invokeRefreshToken(request, response)

      result.statusCode shouldBe HttpStatus.OK
    }

    scenario("invoke refresh token failure") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()

      every { spec.authCookieService.loginWithRefreshCookie(request, response) } returns false

      val result = spec.authController.invokeRefreshToken(request, response)

      result.statusCode shouldBe HttpStatus.BAD_REQUEST
    }
  }
})

private class AuthControllerSpecWrapper(
  val authPasswordService: AuthPasswordService,
  val authCookieService: AuthCookieService
) {
  val authController = AuthController(authPasswordService, authCookieService)
}

private fun getSpec() = AuthControllerSpecWrapper(mockk(), mockk())
