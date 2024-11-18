package cz.tul.backend.book.service.synchronization

import cz.tul.backend.utils.createBookImportDTO
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify

class BookStockSynchronizationServiceTests : FeatureSpec({

  feature("save imported booking") {
    scenario("success") {
      val spec = getSpec()

      val importDTOs = listOf(
        createBookImportDTO(
          categories = "Philosophy, Science",
          authors = "Sun Tzu; Albert Einstein"
        )
      )

      every { spec.bookChunkProcessor.deactivateExistingBooks(importDTOs) } just runs
      every { spec.bookChunkProcessor.processBookChunk(importDTOs) } just runs

      spec.bookStockSynchronizationService.synchronizeBooks(importDTOs)
    }

    scenario("no import books to synchronize") {
      val spec = getSpec()

      spec.bookStockSynchronizationService.synchronizeBooks(emptyList())

      verify(exactly = 0) { spec.bookChunkProcessor.deactivateExistingBooks(any()) }
      verify(exactly = 0) { spec.bookChunkProcessor.processBookChunk(any()) }
    }
  }
})

private class BookImportSynchronizationServiceSpecWrapper(
  val bookChunkProcessor: BookChunkProcessor,
  val chunkSize: Int
) {

  val bookStockSynchronizationService: BookStockSynchronizationService = BookStockSynchronizationService(
    bookChunkProcessor,
    chunkSize
  )
}

private fun getSpec() = BookImportSynchronizationServiceSpecWrapper(
  mockk(),
  10
)
