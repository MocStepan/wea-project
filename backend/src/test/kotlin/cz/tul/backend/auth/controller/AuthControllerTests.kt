package cz.tul.backend.auth.controller

import cz.tul.backend.auth.dto.AuthLoginDTO
import cz.tul.backend.auth.dto.AuthRegisterDTO
import cz.tul.backend.auth.service.AuthCookieService
import cz.tul.backend.auth.service.AuthPasswordService
import cz.tul.backend.auth.valueobject.AuthPasswordServiceRegisterError
import cz.tul.backend.common.serviceresult.ServiceResult
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

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

      every { spec.authCookieService.clearCookies(request, response) } just runs

      spec.authController.logout(request, response)
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

    scenario("register user already exists") {
      val spec = getSpec()
      val authRegisterDTO = mockk<AuthRegisterDTO>()

      every { spec.authPasswordService.register(authRegisterDTO) } returns ServiceResult.Error(
        AuthPasswordServiceRegisterError.USER_ALREADY_EXISTS
      )

      val result = spec.authController.register(authRegisterDTO)

      result.statusCode shouldBe HttpStatus.CONFLICT
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
