package cz.tul.backend.personinfo.controller

import cz.tul.backend.personinfo.dto.PersonInfoDTO
import cz.tul.backend.personinfo.service.PersonInfoService
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication

class PersonInfoControllerTests : FeatureSpec({

  feature("create person info") {
    scenario("success") {
      val spec = createSpec()

      val personInfoDTO = PersonInfoDTO(
        favoriteCategories = setOf()
      )
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.personInfoService.createPersonInfo(personInfoDTO, claims) } returns true

      val response = spec.personInfoController.createPersonInfo(personInfoDTO, authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe true
    }

    scenario("failure") {
      val spec = createSpec()

      val personInfoDTO = PersonInfoDTO(
        favoriteCategories = setOf()
      )
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.personInfoService.createPersonInfo(personInfoDTO, claims) } returns false

      val response = spec.personInfoController.createPersonInfo(personInfoDTO, authentication)

      response.statusCode shouldBe HttpStatus.BAD_REQUEST
      response.body shouldBe false
    }
  }

  feature("get person info") {
    scenario("success") {
      val spec = createSpec()

      val claims = createUserClaims()
      val authentication = mockk<Authentication>()
      val personInfoDTO = PersonInfoDTO(
        favoriteCategories = setOf()
      )

      every { authentication.principal } returns claims
      every { spec.personInfoService.getPersonInfo(claims) } returns personInfoDTO

      val response = spec.personInfoController.getPersonInfo(authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe PersonInfoDTO(
        favoriteCategories = setOf()
      )
    }

    scenario("failure") {
      val spec = createSpec()

      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.personInfoService.getPersonInfo(claims) } returns null

      val response = spec.personInfoController.getPersonInfo(authentication)

      response.statusCode shouldBe HttpStatus.NOT_FOUND
      response.body shouldBe null
    }
  }

  feature("update person info") {
    scenario("success") {
      val spec = createSpec()

      val personInfoDTO = PersonInfoDTO(
        favoriteCategories = setOf()
      )
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.personInfoService.updatePersonInfo(personInfoDTO, claims) } returns true

      val response = spec.personInfoController.updatePersonInfo(personInfoDTO, authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe true
    }

    scenario("failure") {
      val spec = createSpec()

      val personInfoDTO = PersonInfoDTO(
        favoriteCategories = setOf()
      )
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.personInfoService.updatePersonInfo(personInfoDTO, claims) } returns false

      val response = spec.personInfoController.updatePersonInfo(personInfoDTO, authentication)

      response.statusCode shouldBe HttpStatus.BAD_REQUEST
      response.body shouldBe false
    }
  }
})

private class PersonInfoControllerSpecWrapper(
  val personInfoService: PersonInfoService
) {
  val personInfoController = PersonInfoController(personInfoService)
}

private fun createSpec() = PersonInfoControllerSpecWrapper(mockk())
