package cz.tul.backend.book.service

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.book.dto.BookAuthorOptionView
import cz.tul.backend.book.dto.BookCategoryOptionView
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.entity.BookImport
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.repository.BookImportRepository
import cz.tul.backend.utils.objectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class BookServiceTests : FeatureSpec({

  feature("save imported booking") {
    scenario("success") {
      val spec = getSpec()

      val importDTOs = listOf(
        BookImportDTO(
          isbn13 = "978-1-56619-909-4",
          isbn10 = "156619909X",
          title = "The Art of War",
          categories = "Philosophy",
          subtitle = "The Ancient Classic",
          authors = "Sun Tzu",
          thumbnail = "http://books.google.com/books/content?id=1-56619-909-4&printsec=frontcover&img=1&",
          description = "The Art of War is an ancient Chinese military treatise dating from the Late Spring.",
          publishedYear = -500,
          averageRating = 4.5,
          numPages = 48,
          ratingsCount = 3
        )
      )

      val slot = slot<BookImport>()

      every { spec.bookImportRepository.save(capture(slot)) } answers { firstArg() }

      spec.bookService.saveImportedBooks(importDTOs)

      val captured = slot.captured
      captured.content shouldBe objectMapper.writeValueAsBytes(importDTOs)
    }
  }

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
})

private class BookServiceSpecWrapper(
  val objectMapper: ObjectMapper,
  val bookImportRepository: BookImportRepository,
  val bookCategoryRepository: BookCategoryRepository,
  val bookAuthorRepository: BookAuthorRepository
) {

  val bookService: BookService = BookService(
    objectMapper,
    bookImportRepository,
    bookCategoryRepository,
    bookAuthorRepository
  )
}

private fun getSpec() = BookServiceSpecWrapper(objectMapper, mockk(), mockk(), mockk())
