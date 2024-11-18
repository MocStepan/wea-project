package cz.tul.backend.auth.service

import cz.tul.backend.audit.service.BookStockAuditService
import cz.tul.backend.audit.valueobject.AuditType
import cz.tul.backend.auth.base.valueobject.AuthRole
import cz.tul.backend.auth.base.valueobject.EmailAddress
import cz.tul.backend.auth.dto.AuthLoginDTO
import cz.tul.backend.auth.dto.AuthRegisterDTO
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.repository.AuthUserRepository
import cz.tul.backend.auth.valueobject.AuthPasswordServiceRegisterError
import cz.tul.backend.auth.valueobject.Hashed
import cz.tul.backend.common.serviceresult.ServiceResult
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class AuthPasswordServiceTests : FeatureSpec({

  feature("login") {
    scenario("success login with remember me") {
      val spec = getSpec()

      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser(
        email = EmailAddress("example@example.com")
      )
      val loginDTO = AuthLoginDTO(
        EmailAddress("example@example.com"),
        "password",
        true
      )

      every { spec.authUserRepository.findByEmail(loginDTO.email.value) } returns authUser
      every { spec.authPasswordEncoder.matches(loginDTO.password, authUser.password) } returns true
      every { spec.authCookieService.setAccessCookie(authUser, response, true) } just runs
      every { spec.bookStockAuditService.saveAuditLog(AuditType.SIGN_IN, "example@example.com") } just runs

      val result = spec.authPasswordService.login(loginDTO, response)

      result shouldBe true
    }

    scenario("invalid login request") {
      val spec = getSpec()

      val response = mockk<HttpServletResponse>()
      val loginDTO = AuthLoginDTO(
        EmailAddress("example@examp"),
        ""
      )
      val result = spec.authPasswordService.login(loginDTO, response)

      result shouldBe false
    }

    scenario("user not found by email") {
      val spec = getSpec()

      val response = mockk<HttpServletResponse>()
      val loginDTO = AuthLoginDTO(
        EmailAddress("example@example.com"),
        "password",
        true
      )

      every { spec.authUserRepository.findByEmail(loginDTO.email.value) } returns null

      val result = spec.authPasswordService.login(loginDTO, response)

      result shouldBe false
    }

    scenario("invalid password") {
      val spec = getSpec()

      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser(
        email = EmailAddress("example@example.com")
      )
      val loginDTO = AuthLoginDTO(
        EmailAddress("example@example.com"),
        "password",
        true
      )

      every { spec.authUserRepository.findByEmail(loginDTO.email.value) } returns authUser
      every { spec.authPasswordEncoder.matches(loginDTO.password, authUser.password) } returns false

      val result = spec.authPasswordService.login(loginDTO, response)

      result shouldBe false
    }
  }

  feature("register") {
    scenario("succesful registration") {
      val spec = getSpec()

      val registerDTO = AuthRegisterDTO(
        firstName = "John",
        lastName = "Doe",
        email = EmailAddress("john.doe@com.com"),
        password = "password",
        secondPassword = "password"
      )

      val authUserSlot = slot<AuthUser>()

      every { spec.authUserRepository.existsByEmail(registerDTO.email.value) } returns false
      every { spec.authPasswordEncoder.encode(registerDTO.password) } returns Hashed("hashedPassword")
      every { spec.authUserRepository.save(capture(authUserSlot)) } answers { firstArg() }
      every { spec.bookStockAuditService.saveAuditLog(AuditType.SIGN_UP, "john.doe@com.com") } just runs

      val result = spec.authPasswordService.register(registerDTO)

      result shouldBe ServiceResult.Success(true)
      val captured = authUserSlot.captured
      captured.firstName shouldBe registerDTO.firstName
      captured.lastName shouldBe registerDTO.lastName
      captured.email shouldBe registerDTO.email
      captured.password shouldBe Hashed("hashedPassword")
      captured.role shouldBe AuthRole.USER
    }

    scenario("invalid data") {
      val spec = getSpec()

      val registerDTO = AuthRegisterDTO(
        firstName = "John",
        lastName = "Doe",
        email = EmailAddress("john.doe"),
        password = "password",
        secondPassword = "password"
      )

      val result = spec.authPasswordService.register(registerDTO)

      result shouldBe ServiceResult.Error(AuthPasswordServiceRegisterError.INVALID_DATA)
    }

    scenario("user already exists") {
      val spec = getSpec()

      val registerDTO = AuthRegisterDTO(
        firstName = "John",
        lastName = "Doe",
        email = EmailAddress("john.doe@com.com"),
        password = "password",
        secondPassword = "password"
      )

      every { spec.authUserRepository.existsByEmail(registerDTO.email.value) } returns true

      val result = spec.authPasswordService.register(registerDTO)

      result shouldBe ServiceResult.Error(AuthPasswordServiceRegisterError.USER_ALREADY_EXISTS)
    }
  }

  feature("logout") {
    scenario("logout") {
      val spec = getSpec()

      val response = mockk<HttpServletResponse>()
      val request = mockk<HttpServletRequest>()
      val claims = createUserClaims()

      every { spec.bookStockAuditService.saveAuditLog(AuditType.SIGN_OUT, claims) } just runs
      every { spec.authCookieService.clearCookies(request, response) } just runs

      spec.authPasswordService.logout(request, response, claims)
    }
  }
})

private class AuthPasswordServiceSpecWrapper(
  val authUserRepository: AuthUserRepository,
  val authPasswordEncoder: AuthPasswordEncoder,
  val authCookieService: AuthCookieService,
  val bookStockAuditService: BookStockAuditService
) {
  val authPasswordService = AuthPasswordService(
    authUserRepository,
    authPasswordEncoder,
    authCookieService,
    bookStockAuditService
  )
}

private fun getSpec() = AuthPasswordServiceSpecWrapper(mockk(), mockk(), mockk(), mockk())
