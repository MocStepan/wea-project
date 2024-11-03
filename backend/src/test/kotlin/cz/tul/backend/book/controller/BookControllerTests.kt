package cz.tul.backend.book.controller

import cz.tul.backend.book.dto.BookAuthorOptionView
import cz.tul.backend.book.dto.BookCategoryOptionView
import cz.tul.backend.book.dto.BookCommentCreateDTO
import cz.tul.backend.book.dto.BookDetailDTO
import cz.tul.backend.book.dto.BookFilterDTO
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.dto.BookTableDTO
import cz.tul.backend.book.service.BookCommentService
import cz.tul.backend.book.service.BookFilterService
import cz.tul.backend.book.service.BookImportService
import cz.tul.backend.book.service.BookService
import cz.tul.backend.utils.createPageResponseDTO
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication

class BookControllerTests : FeatureSpec({

  feature("filter books") {
    scenario("success") {
      val spec = getSpec()

      val filterDTO = BookFilterDTO()
      val pageResponseDTO = createPageResponseDTO(emptyList<BookTableDTO>())

      every { spec.bookFilterService.filterBooks(filterDTO) } returns pageResponseDTO

      val response = spec.bookController.filterBooks(filterDTO)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe pageResponseDTO
    }
  }

  feature("import books") {
    scenario("success") {
      val spec = getSpec()

      val importDTOs = emptyList<BookImportDTO>()

      every { spec.bookImportService.saveImportedBooks(importDTOs) } just runs

      val response = spec.bookController.importBooks(importDTOs)

      response.statusCode shouldBe HttpStatus.OK
    }
  }

  feature("get all categories") {
    scenario("success") {
      val spec = getSpec()

      val categories = setOf(BookCategoryOptionView("Fantasy"))

      every { spec.bookService.getAllCategories() } returns categories

      val response = spec.bookController.getCategoryOptionViews()

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe categories
    }
  }

  feature("get all authors") {
    scenario("success") {
      val spec = getSpec()

      val authors = setOf(BookAuthorOptionView("Fantasy"))

      every { spec.bookService.getAllAuthors() } returns authors

      val response = spec.bookController.getAuthorOptionViews()

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe authors
    }
  }

  feature("get book detail") {
    scenario("success") {
      val spec = getSpec()

      val bookDetailDTO = mockk<BookDetailDTO>()

      every { spec.bookService.getBookDetail(1L) } returns bookDetailDTO

      val response = spec.bookController.getBookDetail(1L)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe bookDetailDTO
    }

    scenario("book not found") {
      val spec = getSpec()

      every { spec.bookService.getBookDetail(1L) } returns null

      val response = spec.bookController.getBookDetail(1L)

      response.statusCode shouldBe HttpStatus.NOT_FOUND
      response.body shouldBe null
    }
  }

  feature("create book comment") {
    scenario("success") {
      val spec = getSpec()

      val createDTO = BookCommentCreateDTO("comment")
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookCommentService.createBookComment(1L, createDTO, claims) } returns true

      val response = spec.bookController.createBookComment(1L, createDTO, authentication)

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe true
    }

    scenario("book comment creation failed") {
      val spec = getSpec()

      val createDTO = BookCommentCreateDTO("comment")
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.bookCommentService.createBookComment(1L, createDTO, claims) } returns false

      val response = spec.bookController.createBookComment(1L, createDTO, authentication)

      response.statusCode shouldBe HttpStatus.BAD_REQUEST
      response.body shouldBe false
    }
  }
})

private class BookControllerSpecWrapper(
  val bookFilterService: BookFilterService,
  val bookService: BookService,
  val bookImportService: BookImportService,
  val bookCommentService: BookCommentService
) {

  val bookController: BookController = BookController(
    bookFilterService,
    bookService,
    bookImportService,
    bookCommentService
  )
}

private fun getSpec() = BookControllerSpecWrapper(mockk(), mockk(), mockk(), mockk())
