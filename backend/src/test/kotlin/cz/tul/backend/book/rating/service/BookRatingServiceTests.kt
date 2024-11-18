package cz.tul.backend.book.rating.service

import cz.tul.backend.auth.service.AuthUserService
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookRating
import cz.tul.backend.book.rating.dto.BookRatingCreateDTO
import cz.tul.backend.book.rating.dto.BookRatingDTO
import cz.tul.backend.book.rating.repository.BookRatingRepository
import cz.tul.backend.book.repository.BookRepository
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createBook
import cz.tul.backend.utils.createBookRating
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class BookRatingServiceTests : FeatureSpec({

  feature("get book rating") {
    scenario("success") {
      val spec = getSpec()

      val claims = createUserClaims()
      val bookRating = createBookRating(rating = 5.0)

      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns bookRating

      val result = spec.bookRatingService.getBookRating(0L, claims)

      result shouldBe BookRatingDTO(5.0)
    }

    scenario("book rating not found") {
      val spec = getSpec()

      val claims = createUserClaims()

      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns null

      val result = spec.bookRatingService.getBookRating(0L, claims)

      result shouldBe null
    }
  }

  feature("create book rating") {
    scenario("success") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)
      val book = createBook(
        ratingsCount = 1,
        averageRating = 0.0
      )
      val createDTO = BookRatingCreateDTO(5.0)

      val bookSlot = slot<Book>()
      val bookRatingSlot = slot<BookRating>()

      every { spec.bookRatingRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns false
      every { spec.bookRepository.findByIdOrNull(0L) } returns book
      every { spec.authUserService.getReferenceIfExists(0L) } returns authUser
      every { spec.bookRepository.save(capture(bookSlot)) } answers { firstArg() }
      every { spec.bookRatingRepository.save(capture(bookRatingSlot)) } answers { firstArg() }

      val result = spec.bookRatingService.createBookRating(0L, createDTO, claims)

      result shouldBe true
      bookSlot.captured.averageRating shouldBe 2.5
      bookSlot.captured.ratingsCount shouldBe 2
      bookRatingSlot.captured.rating shouldBe 5.0
      bookRatingSlot.captured.authUser shouldBe authUser
      bookRatingSlot.captured.book shouldBe book
    }

    scenario("book rating already exists") {
      val spec = getSpec()

      val claims = createUserClaims()
      val createDTO = BookRatingCreateDTO(5.0)

      every { spec.bookRatingRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns true

      val result = spec.bookRatingService.createBookRating(0L, createDTO, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookRepository.save(any()) }
      verify(exactly = 0) { spec.bookRatingRepository.save(any()) }
    }

    scenario("auth user not found") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)
      val createDTO = BookRatingCreateDTO(5.0)

      every { spec.bookRatingRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns false
      every { spec.authUserService.getReferenceIfExists(0L) } returns null

      val result = spec.bookRatingService.createBookRating(0L, createDTO, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookRepository.save(any()) }
      verify(exactly = 0) { spec.bookRatingRepository.save(any()) }
    }

    scenario("book not found") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)
      val createDTO = BookRatingCreateDTO(5.0)

      every { spec.bookRatingRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns false
      every { spec.authUserService.getReferenceIfExists(0L) } returns authUser
      every { spec.bookRepository.findByIdOrNull(0L) } returns null

      val result = spec.bookRatingService.createBookRating(0L, createDTO, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookRepository.save(any()) }
      verify(exactly = 0) { spec.bookRatingRepository.save(any()) }
    }

    scenario("book is disabled") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)
      val book = createBook(
        disabled = true
      )
      val createDTO = BookRatingCreateDTO(5.0)

      every { spec.bookRatingRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns false
      every { spec.authUserService.getReferenceIfExists(0L) } returns authUser
      every { spec.bookRepository.findByIdOrNull(0L) } returns book

      val result = spec.bookRatingService.createBookRating(0L, createDTO, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookRepository.save(any()) }
      verify(exactly = 0) { spec.bookRatingRepository.save(any()) }
    }
  }

  feature("edit book rating") {
    scenario("success with lower rating") {
      val spec = getSpec()

      val claims = createUserClaims()
      val book = createBook(
        ratingsCount = 2,
        averageRating = 5.0
      )
      val bookRating = createBookRating(
        rating = 5.0,
        book = book
      )
      val createDTO = BookRatingCreateDTO(2.0)

      val bookSlot = slot<Book>()
      val bookRatingSlot = slot<BookRating>()

      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns bookRating
      every { spec.bookRepository.findByIdOrNull(0L) } returns book
      every { spec.bookRatingRepository.save(capture(bookRatingSlot)) } answers { firstArg() }
      every { spec.bookRepository.save(capture(bookSlot)) } answers { firstArg() }

      val result = spec.bookRatingService.editBookRating(0L, createDTO, claims)

      result shouldBe true
      bookSlot.captured.averageRating shouldBe 3.5
      bookSlot.captured.ratingsCount shouldBe 2
      bookRatingSlot.captured.rating shouldBe 2.0
    }

    scenario("success with higher rating") {
      val spec = getSpec()

      val claims = createUserClaims()
      val book = createBook(
        ratingsCount = 2,
        averageRating = 3.5
      )
      val bookRating = createBookRating(
        rating = 2.0,
        book = book
      )
      val createDTO = BookRatingCreateDTO(5.0)

      val bookSlot = slot<Book>()
      val bookRatingSlot = slot<BookRating>()

      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns bookRating
      every { spec.bookRepository.findByIdOrNull(0L) } returns book
      every { spec.bookRatingRepository.save(capture(bookRatingSlot)) } answers { firstArg() }
      every { spec.bookRepository.save(capture(bookSlot)) } answers { firstArg() }

      val result = spec.bookRatingService.editBookRating(0L, createDTO, claims)

      result shouldBe true
      bookSlot.captured.averageRating shouldBe 5.0
      bookSlot.captured.ratingsCount shouldBe 2
      bookRatingSlot.captured.rating shouldBe 5.0
    }

    scenario("book rating not found") {
      val spec = getSpec()

      val claims = createUserClaims()
      val createDTO = BookRatingCreateDTO(5.0)

      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns null

      val result = spec.bookRatingService.editBookRating(0L, createDTO, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookRepository.save(any()) }
      verify(exactly = 0) { spec.bookRatingRepository.save(any()) }
    }

    scenario("book not found") {
      val spec = getSpec()

      val claims = createUserClaims()
      val bookRating = createBookRating()
      val createDTO = BookRatingCreateDTO(5.0)

      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns bookRating
      every { spec.bookRepository.findByIdOrNull(0L) } returns null

      val result = spec.bookRatingService.editBookRating(0L, createDTO, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookRepository.save(any()) }
      verify(exactly = 0) { spec.bookRatingRepository.save(any()) }
    }
  }

  feature("delete book rating") {
    scenario("success") {
      val spec = getSpec()

      val claims = createUserClaims()
      val book = createBook(
        ratingsCount = 1,
        averageRating = 5.0
      )
      val bookRating = createBookRating(
        rating = 5.0,
        book = book
      )

      val bookSlot = slot<Book>()

      every { spec.bookRepository.findByIdOrNull(0L) } returns book
      every { spec.bookRepository.save(capture(bookSlot)) } answers { firstArg() }
      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns bookRating
      every { spec.bookRatingRepository.delete(bookRating) } just runs

      val result = spec.bookRatingService.deleteBookRating(0L, claims)

      result shouldBe true
      bookSlot.captured.averageRating shouldBe 0.0
      bookSlot.captured.ratingsCount shouldBe 0
    }

    scenario("book rating not found") {
      val spec = getSpec()

      val claims = createUserClaims()
      val book = createBook()

      every { spec.bookRepository.findByIdOrNull(0L) } returns book
      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns null

      val result = spec.bookRatingService.deleteBookRating(0L, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookRepository.save(any()) }
      verify(exactly = 0) { spec.bookRatingRepository.delete(any()) }
    }

    scenario("book not found") {
      val spec = getSpec()

      val claims = createUserClaims()
      val bookRating = createBookRating()

      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns bookRating
      every { spec.bookRepository.findByIdOrNull(0L) } returns null

      val result = spec.bookRatingService.deleteBookRating(0L, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookRepository.save(any()) }
      verify(exactly = 0) { spec.bookRatingRepository.delete(any()) }
    }

    scenario("book is disabled") {
      val spec = getSpec()

      val claims = createUserClaims()
      val book = createBook(
        disabled = true
      )
      val bookRating = createBookRating()

      every { spec.bookRatingRepository.findByAuthUser_IdAndBook_Id(0L, 0L) } returns bookRating
      every { spec.bookRepository.findByIdOrNull(0L) } returns book

      val result = spec.bookRatingService.deleteBookRating(0L, claims)

      result shouldBe false
      verify(exactly = 0) { spec.bookRepository.save(any()) }
      verify(exactly = 0) { spec.bookRatingRepository.delete(any()) }
    }
  }
})

private class BookRatingServiceSpecWrapper(
  val bookRepository: BookRepository,
  val bookRatingRepository: BookRatingRepository,
  val authUserService: AuthUserService
) {

  val bookRatingService: BookRatingService = BookRatingService(
    bookRepository,
    bookRatingRepository,
    authUserService
  )
}

private fun getSpec() = BookRatingServiceSpecWrapper(mockk(), mockk(), mockk())
