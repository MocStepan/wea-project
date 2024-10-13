package cz.tul.backend.book.service

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.entity.BookImport
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.repository.BookImportRepository
import cz.tul.backend.book.repository.BookRepository
import cz.tul.backend.utils.objectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot

class BookImportSynchronizationServiceTests : FeatureSpec({

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
          thumbnail = "http://books.google.com/books/content?id=1-56619-909-4&printsec=frontcover&img=1&zoom",
          description = "The Art of War is an ancient Chinese military treatise dating from the Late Spring.",
          publishedYear = -500,
          averageRating = 4.5,
          numPages = 48,
          ratingsCount = 3
        )
      )

      val importBook = BookImport(
        id = 1,
        content = objectMapper.writeValueAsString(importDTOs)
      )

      every { spec.bookImportRepository.findAll() } returns listOf(importBook)

      val bookSlot = slot<Book>()
      val bookAuthorSlot = slot<BookAuthor>()
      val bookCategorySlot = slot<BookCategory>()

      every { spec.bookRepository.save(capture(bookSlot)) } answers { firstArg() }
      every { spec.bookAuthorRepository.existsByBook_IdAndName(any(), "Sun Tzu") } returns false
      every { spec.bookAuthorRepository.save(capture(bookAuthorSlot)) } answers { firstArg() }
      every { spec.bookCategoryRepository.existsByBook_IdAndName(any(), "Philosophy") } returns false
      every { spec.bookCategoryRepository.save(capture(bookCategorySlot)) } answers { firstArg() }
      every { spec.bookImportRepository.deleteAll() } just runs

      spec.bookImportSynchronizationService.synchronizeBooks()

      bookSlot.captured.isbn13 shouldBe importDTOs[0].isbn13
      bookSlot.captured.isbn10 shouldBe importDTOs[0].isbn10
      bookSlot.captured.title shouldBe importDTOs[0].title
      bookSlot.captured.subtitle shouldBe importDTOs[0].subtitle
      bookSlot.captured.thumbnail shouldBe importDTOs[0].thumbnail
      bookSlot.captured.description shouldBe importDTOs[0].description
      bookSlot.captured.publishedYear shouldBe importDTOs[0].publishedYear
      bookSlot.captured.averageRating shouldBe importDTOs[0].averageRating
      bookSlot.captured.numPages shouldBe importDTOs[0].numPages
      bookSlot.captured.ratingsCount shouldBe importDTOs[0].ratingsCount

      bookAuthorSlot.captured.name shouldBe importDTOs[0].authors
      bookCategorySlot.captured.name shouldBe importDTOs[0].categories
    }

    scenario("no books to synchronize") {
      val spec = getSpec()

      every { spec.bookImportRepository.findAll() } returns emptyList()

      spec.bookImportSynchronizationService.synchronizeBooks()
    }
  }
})

private class BookImportSynchronizationServiceSpecWrapper(
  val bookImportRepository: BookImportRepository,
  val bookRepository: BookRepository,
  val bookAuthorRepository: BookAuthorRepository,
  val bookCategoryRepository: BookCategoryRepository,
  val objectMapper: ObjectMapper
) {

  val bookImportSynchronizationService: BookImportSynchronizationService = BookImportSynchronizationService(
    bookImportRepository,
    bookRepository,
    bookAuthorRepository,
    bookCategoryRepository,
    objectMapper
  )
}

private fun getSpec() = BookImportSynchronizationServiceSpecWrapper(
  mockk(),
  mockk(),
  mockk(),
  mockk(),
  objectMapper
)
