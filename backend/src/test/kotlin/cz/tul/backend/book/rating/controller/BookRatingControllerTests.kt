package cz.tul.backend.book.rating.controller

import cz.tul.backend.book.rating.dto.BookRatingCreateDTO
import cz.tul.backend.book.rating.dto.BookRatingDTO
import cz.tul.backend.book.rating.service.BookRatingService
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication

class BookRatingControllerTests : FeatureSpec({

  feature("get book rating") {
    scenario("success") {
      val spec = getSpec()

      val createDTO = BookRatingDTO(5.0)
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookRatingService.getBookRating(1L, claims) } returns createDTO

      val response = spec.bookRatingController.getBookRating(1L, authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe createDTO
    }

    scenario("book rating not found") {
      val spec = getSpec()

      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookRatingService.getBookRating(1L, claims) } returns null

      val response = spec.bookRatingController.getBookRating(1L, authentication)

      response.statusCode shouldBe HttpStatus.NO_CONTENT
      response.body shouldBe null
    }
  }

  feature("create book rating") {
    scenario("success") {
      val spec = getSpec()

      val createDTO = BookRatingCreateDTO(5.0)
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookRatingService.createBookRating(1L, createDTO, claims) } returns true

      val response = spec.bookRatingController.createBookRating(1L, createDTO, authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe true
    }

    scenario("book rating addition failed") {
      val spec = getSpec()

      val createDTO = BookRatingCreateDTO(5.0)
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookRatingService.createBookRating(1L, createDTO, claims) } returns false

      val response = spec.bookRatingController.createBookRating(1L, createDTO, authentication)

      response.statusCode shouldBe HttpStatus.BAD_REQUEST
      response.body shouldBe false
    }
  }

  feature("edit book rating") {
    scenario("success") {
      val spec = getSpec()

      val createDTO = BookRatingCreateDTO(5.0)
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookRatingService.editBookRating(1L, createDTO, claims) } returns true

      val response = spec.bookRatingController.editBookRating(1L, createDTO, authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe true
    }

    scenario("book rating addition failed") {
      val spec = getSpec()

      val createDTO = BookRatingCreateDTO(5.0)
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookRatingService.editBookRating(1L, createDTO, claims) } returns false

      val response = spec.bookRatingController.editBookRating(1L, createDTO, authentication)

      response.statusCode shouldBe HttpStatus.BAD_REQUEST
      response.body shouldBe false
    }
  }

  feature("delete book rating") {
    scenario("success") {
      val spec = getSpec()

      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookRatingService.deleteBookRating(1L, claims) } returns true

      val response = spec.bookRatingController.deleteBookRating(1L, authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe true
    }

    scenario("book rating deletion failed") {
      val spec = getSpec()

      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookRatingService.deleteBookRating(1L, claims) } returns false

      val response = spec.bookRatingController.deleteBookRating(1L, authentication)

      response.statusCode shouldBe HttpStatus.BAD_REQUEST
      response.body shouldBe false
    }
  }
})

private class BookRatingControllerSpecWrapper(
  val bookRatingService: BookRatingService
) {
  val bookRatingController = BookRatingController(bookRatingService)
}

private fun getSpec() = BookRatingControllerSpecWrapper(mockk())
