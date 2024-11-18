package cz.tul.backend.book.favorite.service

import cz.tul.backend.auth.service.AuthUserService
import cz.tul.backend.book.entity.BookFavorite
import cz.tul.backend.book.favorite.repository.BookFavoriteRepository
import cz.tul.backend.book.repository.BookRepository
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createBook
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class BookFavoriteServiceTests : FeatureSpec({

  feature("add book to favorite") {
    scenario("success") {
      val spec = getSpec()

      val book = createBook()
      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)

      val bookFavoriteSlot = slot<BookFavorite>()

      every { spec.bookFavoriteRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns false
      every { spec.authUserService.getReferenceIfExists(0L) } returns authUser
      every { spec.bookRepository.findByIdOrNull(0L) } returns book
      every { spec.bookFavoriteRepository.save(capture(bookFavoriteSlot)) } answers { firstArg() }

      val result = spec.bookFavoriteService.addBookToFavorite(0L, claims)

      result shouldBe true
      bookFavoriteSlot.captured.book shouldBe book
      bookFavoriteSlot.captured.authUser shouldBe authUser
    }

    scenario("book was already added to favorites") {
      val spec = getSpec()

      val claims = createUserClaims()

      every { spec.bookFavoriteRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns true

      val result = spec.bookFavoriteService.addBookToFavorite(0L, claims)

      result shouldBe false
    }

    scenario("auth user not found") {
      val spec = getSpec()

      val claims = createUserClaims()

      every { spec.bookFavoriteRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns false
      every { spec.authUserService.getReferenceIfExists(0L) } returns null

      val result = spec.bookFavoriteService.addBookToFavorite(0L, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookFavoriteRepository.save(any()) }
    }

    scenario("book not found") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)

      every { spec.bookFavoriteRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns false
      every { spec.authUserService.getReferenceIfExists(0L) } returns authUser
      every { spec.bookRepository.findByIdOrNull(0L) } returns null

      val result = spec.bookFavoriteService.addBookToFavorite(0L, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookFavoriteRepository.save(any()) }
    }

    scenario("success") {
      val spec = getSpec()

      val book = createBook(disabled = true)
      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)

      every { spec.bookFavoriteRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns false
      every { spec.authUserService.getReferenceIfExists(0L) } returns authUser
      every { spec.bookRepository.findByIdOrNull(0L) } returns book

      val result = spec.bookFavoriteService.addBookToFavorite(0L, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookFavoriteRepository.save(any()) }
    }
  }
})

private class BookFavoriteServiceSpecWrapper(
  val bookRepository: BookRepository,
  val bookFavoriteRepository: BookFavoriteRepository,
  val authUserService: AuthUserService
) {

  val bookFavoriteService: BookFavoriteService = BookFavoriteService(
    bookRepository,
    bookFavoriteRepository,
    authUserService
  )
}

private fun getSpec() = BookFavoriteServiceSpecWrapper(mockk(), mockk(), mockk())
