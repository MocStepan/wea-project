package cz.tul.backend.book.controller

import cz.tul.backend.book.dto.BookFilterDTO
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.dto.BookTableDTO
import cz.tul.backend.book.service.BookFilterService
import cz.tul.backend.book.service.BookService
import cz.tul.backend.utils.createPageResponseDTO
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.springframework.http.HttpStatus

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

      every { spec.bookService.saveImportedBooks(importDTOs) } just runs

      val response = spec.bookController.importBooks(importDTOs)

      response.statusCode shouldBe HttpStatus.OK
    }
  }

  feature("get all categories") {
    scenario("success") {
      val spec = getSpec()

      val categories = setOf("Philosophy", "Science")

      every { spec.bookService.getAllCategories() } returns categories

      val response = spec.bookController.getAllCategories()

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe categories
    }
  }

  feature("get all authors") {
    scenario("success") {
      val spec = getSpec()

      val authors = setOf("Sun Tzu")

      every { spec.bookService.getAllAuthors() } returns authors

      val response = spec.bookController.getAllAuthors()

      response.statusCode shouldBe HttpStatus.OK
      response.body shouldBe authors
    }
  }
})

private class BookControllerSpecWrapper(
  val bookFilterService: BookFilterService,
  val bookService: BookService
) {

  val bookController: BookController = BookController(bookFilterService, bookService)
}

private fun getSpec() = BookControllerSpecWrapper(mockk(), mockk())
