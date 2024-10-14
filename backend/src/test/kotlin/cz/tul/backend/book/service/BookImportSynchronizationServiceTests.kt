package cz.tul.backend.book.service

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookAuthorLink
import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.entity.BookCategoryLink
import cz.tul.backend.book.entity.BookImport
import cz.tul.backend.book.repository.BookAuthorLinkRepository
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryLinkRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.repository.BookImportRepository
import cz.tul.backend.book.repository.BookRepository
import cz.tul.backend.utils.createBookImportDTO
import cz.tul.backend.utils.objectMapper
import cz.tul.backend.utils.processTransaction
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.springframework.transaction.support.TransactionTemplate

class BookImportSynchronizationServiceTests : FeatureSpec({

  feature("save imported booking") {
    scenario("success") {
      val spec = getSpec()

      val importDTOs = listOf(
        createBookImportDTO(
          categories = "Philosophy, Science",
          authors = "Sun Tzu; Albert Einstein"
        )
      )
      val importBook = BookImport(
        id = 1,
        content = objectMapper.writeValueAsBytes(importDTOs)
      )

      val bookSlot = slot<Book>()
      val bookAuthorSlot = slot<BookAuthor>()
      val bookCategorySlot = slot<BookCategory>()
      val bookAuthorLinkSlot = slot<BookAuthorLink>()
      val bookCategoryLinkSlot = slot<BookCategoryLink>()

      every { spec.bookImportRepository.findAll() } returns listOf(importBook)
      processTransaction(spec.transactionTemplate)
      every { spec.bookRepository.save(capture(bookSlot)) } answers { firstArg() }
      every { spec.bookImportRepository.deleteAll() } just runs

      every { spec.bookCategoryLinkRepository.existsByBook_IdAndCategory_Name(any(), any()) } returns false
      every { spec.bookCategoryRepository.findByName(any()) } returns null
      every { spec.bookCategoryRepository.save(capture(bookCategorySlot)) } answers { firstArg() }
      every { spec.bookCategoryLinkRepository.save(capture(bookCategoryLinkSlot)) } answers { firstArg() }

      every { spec.bookAuthorLinkRepository.existsByBook_IdAndAuthor_Name(any(), any()) } returns false
      every { spec.bookAuthorRepository.findByName(any()) } returns null
      every { spec.bookAuthorRepository.save(capture(bookAuthorSlot)) } answers { firstArg() }
      every { spec.bookAuthorLinkRepository.save(capture(bookAuthorLinkSlot)) } answers { firstArg() }

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

      bookAuthorSlot.captured.name shouldBe "Albert Einstein"
      bookCategorySlot.captured.name shouldBe "Science"

      bookAuthorLinkSlot.captured.book shouldBe bookSlot.captured
      bookAuthorLinkSlot.captured.author shouldBe bookAuthorSlot.captured

      bookCategoryLinkSlot.captured.book shouldBe bookSlot.captured
      bookCategoryLinkSlot.captured.category shouldBe bookCategorySlot.captured
    }

    scenario("category and author already exists") {
      val spec = getSpec()

      val bookCategory = BookCategory(name = "Philosophy")
      val bookAuthor = BookAuthor(name = "Sun Tzu")
      val importDTOs = listOf(createBookImportDTO())
      val importBook = BookImport(
        id = 1,
        content = objectMapper.writeValueAsBytes(importDTOs)
      )

      every { spec.bookImportRepository.findAll() } returns listOf(importBook)
      processTransaction(spec.transactionTemplate)
      every { spec.bookRepository.save(any()) } answers { firstArg() }
      every { spec.bookImportRepository.deleteAll() } just runs

      every { spec.bookCategoryLinkRepository.existsByBook_IdAndCategory_Name(any(), "Philosophy") } returns false
      every { spec.bookCategoryRepository.findByName("Philosophy") } returns bookCategory
      every { spec.bookCategoryLinkRepository.save(any()) } answers { firstArg() }

      every { spec.bookAuthorLinkRepository.existsByBook_IdAndAuthor_Name(any(), "Sun Tzu") } returns false
      every { spec.bookAuthorRepository.findByName("Sun Tzu") } returns bookAuthor
      every { spec.bookAuthorLinkRepository.save(any()) } answers { firstArg() }

      spec.bookImportSynchronizationService.synchronizeBooks()

      verify(exactly = 0) { spec.bookAuthorRepository.save(any()) }
      verify(exactly = 0) { spec.bookCategoryRepository.save(any()) }
    }

    scenario("category and author links already exists") {
      val spec = getSpec()

      val importDTOs = listOf(createBookImportDTO())
      val importBook = BookImport(
        id = 1,
        content = objectMapper.writeValueAsBytes(importDTOs)
      )

      every { spec.bookImportRepository.findAll() } returns listOf(importBook)
      processTransaction(spec.transactionTemplate)
      every { spec.bookRepository.save(any()) } answers { firstArg() }
      every { spec.bookCategoryLinkRepository.existsByBook_IdAndCategory_Name(any(), "Philosophy") } returns true
      every { spec.bookAuthorLinkRepository.existsByBook_IdAndAuthor_Name(any(), "Sun Tzu") } returns true
      every { spec.bookImportRepository.deleteAll() } just runs

      spec.bookImportSynchronizationService.synchronizeBooks()

      verify(exactly = 0) { spec.bookAuthorRepository.findByName(any()) }
      verify(exactly = 0) { spec.bookCategoryRepository.findByName(any()) }
    }

    scenario("categories and author is null") {
      val spec = getSpec()

      val importDTOs = listOf(
        createBookImportDTO(
          authors = null,
          categories = null
        )
      )
      val importBook = BookImport(
        id = 1,
        content = objectMapper.writeValueAsBytes(importDTOs)
      )

      every { spec.bookImportRepository.findAll() } returns listOf(importBook)
      processTransaction(spec.transactionTemplate)
      every { spec.bookRepository.save(any()) } answers { firstArg() }
      every { spec.bookImportRepository.deleteAll() } just runs

      spec.bookImportSynchronizationService.synchronizeBooks()

      verify(exactly = 0) { spec.bookAuthorLinkRepository.existsByBook_IdAndAuthor_Name(any(), any()) }
      verify(exactly = 0) { spec.bookCategoryLinkRepository.existsByBook_IdAndCategory_Name(any(), any()) }
    }
  }
})

private class BookImportSynchronizationServiceSpecWrapper(
  val bookImportRepository: BookImportRepository,
  val bookRepository: BookRepository,
  val bookAuthorRepository: BookAuthorRepository,
  val bookCategoryRepository: BookCategoryRepository,
  val bookCategoryLinkRepository: BookCategoryLinkRepository,
  val bookAuthorLinkRepository: BookAuthorLinkRepository,
  val objectMapper: ObjectMapper,
  val chunkSize: Int,
  val transactionTemplate: TransactionTemplate
) {

  val bookImportSynchronizationService: BookImportSynchronizationService = BookImportSynchronizationService(
    bookImportRepository,
    bookRepository,
    bookAuthorRepository,
    bookCategoryRepository,
    bookCategoryLinkRepository,
    bookAuthorLinkRepository,
    objectMapper,
    chunkSize,
    transactionTemplate
  )
}

private fun getSpec() = BookImportSynchronizationServiceSpecWrapper(
  mockk(),
  mockk(),
  mockk(),
  mockk(),
  mockk(),
  mockk(),
  objectMapper,
  10,
  mockk()
)
