package cz.tul.backend.book.service

import cz.tul.backend.auth.utils.getAuthUserFullName
import cz.tul.backend.book.dto.BookAuthorOptionView
import cz.tul.backend.book.dto.BookCategoryOptionView
import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.favorite.repository.BookFavoriteRepository
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.repository.BookCommentRepository
import cz.tul.backend.book.repository.BookRepository
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createBook
import cz.tul.backend.utils.createBookComment
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull

class BookServiceTests : FeatureSpec({

  feature("get all categories") {
    scenario("success") {
      val spec = getSpec()

      val bookCategory1 = BookCategory(name = "Philosophy")
      val bookCategory2 = BookCategory(name = "Science")

      every { spec.bookCategoryRepository.findAll() } returns listOf(bookCategory1, bookCategory2)

      val result = spec.bookService.getAllCategories()

      result.size shouldBe 2
      result shouldBe setOf(BookCategoryOptionView("Philosophy"), BookCategoryOptionView("Science"))
    }
  }

  feature("get all authors") {
    scenario("success") {
      val spec = getSpec()

      val bookAuthor = BookAuthor(name = "Plato")

      every { spec.bookAuthorRepository.findAll() } returns listOf(bookAuthor)

      val result = spec.bookService.getAllAuthors()

      result.size shouldBe 1
      result shouldBe setOf(BookAuthorOptionView("Plato"))
    }
  }

  feature("get book detail") {
    scenario("success") {
      val spec = getSpec()

      val book = createBook()
      val authUser = createAuthUser()
      val bookAuthor = BookAuthor(name = "Plato")
      val bookCategory = BookCategory(name = "Philosophy")
      val bookComment = createBookComment(authUser = authUser)
      val claims = createUserClaims(authUser)

      every { spec.bookRepository.findByIdOrNull(0L) } returns book
      every { spec.bookCategoryRepository.findByBookCategoryLink_Book_Id(0L) } returns setOf(bookCategory)
      every { spec.bookAuthorRepository.findByBookAuthorLink_Book_Id(0L) } returns setOf(bookAuthor)
      every { spec.bookCommentRepository.findByBook_Id(0L) } returns setOf(bookComment)
      every { spec.bookFavoriteRepository.existsByAuthUser_IdAndBook_Id(0L, 0L) } returns false

      val result = spec.bookService.getBookDetail(0L, claims)!!

      result.isbn13 shouldBe book.isbn13
      result.isbn10 shouldBe book.isbn10
      result.title shouldBe book.title
      result.categories.size shouldBe 1
      result.categories[0].name shouldBe "Philosophy"
      result.subtitle shouldBe book.subtitle
      result.authors.size shouldBe 1
      result.authors[0].name shouldBe "Plato"
      result.thumbnail shouldBe book.thumbnail
      result.description shouldBe book.description
      result.publishedYear shouldBe book.publishedYear
      result.averageRating shouldBe book.averageRating
      result.numPages shouldBe book.numPages
      result.ratingsCount shouldBe book.ratingsCount
      result.bookComments.size shouldBe 1
      result.bookComments[0].comment shouldBe "Great book!"
      result.bookComments[0].user shouldBe authUser.getAuthUserFullName()
      result.disabled shouldBe book.disabled
      result.favorite shouldBe false
    }

    scenario("book not found") {
      val spec = getSpec()

      every { spec.bookRepository.findByIdOrNull(1L) } returns null

      val result = spec.bookService.getBookDetail(1L, null)

      result shouldBe null
    }
  }

  feature("get book if exist") {
    scenario("success") {
      val spec = getSpec()

      val book = createBook()

      every { spec.bookRepository.findByIdOrNull(0L) } returns book

      val result = spec.bookService.getReferenceIfExists(0L)

      result shouldBe book
    }

    scenario("book not found") {
      val spec = getSpec()

      every { spec.bookRepository.findByIdOrNull(1L) } returns null

      val result = spec.bookService.getReferenceIfExists(1L)

      result shouldBe null
    }
  }
})

private class BookServiceSpecWrapper(
  val bookRepository: BookRepository,
  val bookCommentRepository: BookCommentRepository,
  val bookCategoryRepository: BookCategoryRepository,
  val bookAuthorRepository: BookAuthorRepository,
  val bookFavoriteRepository: BookFavoriteRepository
) {

  val bookService: BookService = BookService(
    bookRepository,
    bookCommentRepository,
    bookCategoryRepository,
    bookAuthorRepository,
    bookFavoriteRepository
  )
}

private fun getSpec() = BookServiceSpecWrapper(mockk(), mockk(), mockk(), mockk(), mockk())
