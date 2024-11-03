package cz.tul.backend.book.service.synchronization

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.BookImport
import cz.tul.backend.book.repository.BookImportRepository
import cz.tul.backend.utils.createBookImportDTO
import cz.tul.backend.utils.objectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify

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
        content = spec.objectMapper.writeValueAsBytes(importDTOs)
      )

      every { spec.bookImportRepository.findAll() } returns listOf(importBook)
      every { spec.bookChunkProcessor.deactivateExistingBooks(importDTOs) } just runs
      every { spec.bookChunkProcessor.processBookChunk(importDTOs) } just runs
      every { spec.bookImportRepository.deleteAll() } just runs

      spec.bookImportSynchronizationService.synchronizeBooks()
    }

    scenario("failed to parse import book content") {
      val spec = getSpec()

      val importBook = BookImport(
        id = 1,
        content = spec.objectMapper.writeValueAsBytes("invalid content")
      )

      every { spec.bookImportRepository.findAll() } returns listOf(importBook)
      shouldThrow<Exception> {
        spec.objectMapper.readValue(importBook.content, object : TypeReference<List<BookImportDTO>>() {})
      }

      spec.bookImportSynchronizationService.synchronizeBooks()

      verify(exactly = 0) { spec.bookImportRepository.deleteAll() }
    }

    scenario("no import books to synchronize") {
      val spec = getSpec()

      every { spec.bookImportRepository.findAll() } returns emptyList()

      spec.bookImportSynchronizationService.synchronizeBooks()

      verify(exactly = 0) { spec.bookImportRepository.deleteAll() }
    }
  }
})

private class BookImportSynchronizationServiceSpecWrapper(
  val bookChunkProcessor: BookChunkProcessor,
  val bookImportRepository: BookImportRepository,
  val objectMapper: ObjectMapper,
  val chunkSize: Int
) {

  val bookImportSynchronizationService: BookImportSynchronizationService = BookImportSynchronizationService(
    bookChunkProcessor,
    bookImportRepository,
    objectMapper,
    chunkSize
  )
}

private fun getSpec() = BookImportSynchronizationServiceSpecWrapper(
  mockk(),
  mockk(),
  objectMapper,
  10
)
