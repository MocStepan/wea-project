package cz.tul.backend.auth.service

import cz.tul.backend.auth.dto.AuthUserDTO
import cz.tul.backend.auth.repository.AuthUserRepository
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication

class AuthUserServiceTests : FeatureSpec({

  feature("get auth user") {
    scenario("success") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val authUserDTO = AuthUserDTO.from(authUser)
      val authJwtClaims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns authJwtClaims
      every { spec.authUserRepository.findByIdOrNull(authJwtClaims.authUserId) } returns authUser

      val result = spec.authUserService.getAuthUser(authJwtClaims)

      result shouldBe authUserDTO
    }

    scenario("failure") {
      val spec = getSpec()

      val authJwtClaims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns authJwtClaims
      every { spec.authUserRepository.findByIdOrNull(authJwtClaims.authUserId) } returns null

      val result = spec.authUserService.getAuthUser(authJwtClaims)

      result shouldBe null
    }
  }
})

private class AuthUserServiceSpecWrapper(
  val authUserRepository: AuthUserRepository
) {
  val authUserService = AuthUserService(
    authUserRepository
  )
}

private fun getSpec() = AuthUserServiceSpecWrapper(mockk())
