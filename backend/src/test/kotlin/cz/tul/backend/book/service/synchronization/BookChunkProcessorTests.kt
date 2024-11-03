package cz.tul.backend.book.service.synchronization

import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.repository.BookRepository
import cz.tul.backend.utils.createBook
import cz.tul.backend.utils.createBookImportDTO
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify

class BookChunkProcessorTests : FeatureSpec({

  feature("process book chunk") {
    scenario("processes book chunk with new book") {
      val spec = getSpec()

      val bookImportDTO = createBookImportDTO()
      val bookChunk = listOf(bookImportDTO)
      val bookSlot = slot<Book>()

      every { spec.bookRepository.findByIsbn13(bookImportDTO.isbn13) } returns null
      every { spec.bookRepository.save(capture(bookSlot)) } answers { firstArg() }
      every { spec.bookCategoryImportComponent.processBookCategories(bookImportDTO.categories, any()) } just runs
      every { spec.bookAuthorImportComponent.processBookAuthors(bookImportDTO.authors, any()) } just runs

      spec.bookChunkProcessor.processBookChunk(bookChunk)

      val captured = bookSlot.captured
      captured.isbn13 shouldBe bookImportDTO.isbn13
      captured.isbn10 shouldBe bookImportDTO.isbn10
      captured.title shouldBe bookImportDTO.title
      captured.subtitle shouldBe bookImportDTO.subtitle
      captured.thumbnail shouldBe bookImportDTO.thumbnail
      captured.description shouldBe bookImportDTO.description
      captured.publishedYear shouldBe bookImportDTO.publishedYear
      captured.averageRating shouldBe bookImportDTO.averageRating
      captured.numPages shouldBe bookImportDTO.numPages
      captured.ratingsCount shouldBe bookImportDTO.ratingsCount
      captured.disabled shouldBe false

      verify(exactly = 1) { spec.bookCategoryImportComponent.processBookCategories(bookImportDTO.categories, captured) }
      verify(exactly = 1) { spec.bookAuthorImportComponent.processBookAuthors(bookImportDTO.authors, captured) }
    }
  }

  feature("deactivate existing books") {
    scenario("deactivates existing books") {
      val spec = getSpec()

      val bookImportDTO = createBookImportDTO()
      val bookChunk = listOf(bookImportDTO)
      val book = createBook()
      val bookSlot = slot<Book>()

      every { spec.bookRepository.findByIsbn13NotIn(bookChunk.map { it.isbn13 }) } returns listOf(book)
      every { spec.bookRepository.save(capture(bookSlot)) } answers { firstArg() }

      spec.bookChunkProcessor.deactivateExistingBooks(bookChunk)

      val captured = bookSlot.captured
      captured.disabled shouldBe true
    }
  }
})

private class BookChunkProcessorSpecWrapper(
  val bookCategoryImportComponent: BookCategoryImportComponent,
  val bookAuthorImportComponent: BookAuthorImportComponent,
  val bookRepository: BookRepository
) {
  val bookChunkProcessor = BookChunkProcessor(bookCategoryImportComponent, bookAuthorImportComponent, bookRepository)
}

private fun getSpec() = BookChunkProcessorSpecWrapper(
  mockk(),
  mockk(),
  mockk()
)
