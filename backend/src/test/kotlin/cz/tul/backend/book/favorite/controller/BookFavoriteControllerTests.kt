package cz.tul.backend.book.favorite.controller

import cz.tul.backend.book.favorite.service.BookFavoriteService
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication

class BookFavoriteControllerTests : FeatureSpec({

  feature("add book to favorite") {
    scenario("success") {
      val spec = getSpec()

      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookFavoriteService.addBookToFavorite(1L, claims) } returns true

      val response = spec.bookFavoriteController.addBookToFavorite(1L, authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe true
    }

    scenario("book was already added to favorites") {
      val spec = getSpec()

      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookFavoriteService.addBookToFavorite(1L, claims) } returns false

      val response = spec.bookFavoriteController.addBookToFavorite(1L, authentication)

      response.statusCode shouldBe HttpStatus.BAD_REQUEST
      response.body shouldBe false
    }
  }

  feature("delete book from favorite") {
    scenario("success") {
      val spec = getSpec()

      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookFavoriteService.deleteBookFromFavorite(1L, claims) } returns true

      val response = spec.bookFavoriteController.deleteBookFromFavorite(1L, authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe true
    }

    scenario("book was not added to favorites") {
      val spec = getSpec()

      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookFavoriteService.deleteBookFromFavorite(1L, claims) } returns false

      val response = spec.bookFavoriteController.deleteBookFromFavorite(1L, authentication)

      response.statusCode shouldBe HttpStatus.BAD_REQUEST
      response.body shouldBe false
    }
  }
})

private class BookFavoriteControllerSpecWrapper(
  val bookFavoriteService: BookFavoriteService
) {

  val bookFavoriteController: BookFavoriteController = BookFavoriteController(bookFavoriteService)
}

private fun getSpec() = BookFavoriteControllerSpecWrapper(mockk())
