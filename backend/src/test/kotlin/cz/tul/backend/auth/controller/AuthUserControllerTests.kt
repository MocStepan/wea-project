package cz.tul.backend.auth.controller

import cz.tul.backend.auth.dto.AuthUserDTO
import cz.tul.backend.auth.service.AuthUserService
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication

class AuthUserControllerTests : FeatureSpec({

  feature("get auth user") {
    scenario("success") {
      val spec = getSpec()

      val authJwtClaims = createUserClaims()
      val authentication = mockk<Authentication>()
      val authUserDTO = AuthUserDTO(
        firstName = "John",
        lastName = "Doe"
      )

      every { authentication.principal } returns authJwtClaims
      every { spec.authUserService.getAuthUser(authJwtClaims) } returns authUserDTO

      val result = spec.authUserController.getAuthUser(authentication)

      result.statusCode shouldBe HttpStatus.OK
      result.body shouldBe authUserDTO
    }

    scenario("failure") {
      val spec = getSpec()

      val authJwtClaims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns authJwtClaims
      every { spec.authUserService.getAuthUser(authJwtClaims) } returns null

      val result = spec.authUserController.getAuthUser(authentication)

      result.statusCode shouldBe HttpStatus.NOT_FOUND
      result.body shouldBe null
    }
  }
})

private class AuthUserControllerSpecWrapper(
  val authUserService: AuthUserService
) {
  val authUserController = AuthUserController(authUserService)
}

private fun getSpec() = AuthUserControllerSpecWrapper(mockk())
