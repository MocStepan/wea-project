package cz.tul.backend.book.service.synchronization

import cz.tul.backend.book.dto.BookImportDTO
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Service for synchronizing books from import table to book table.
 */
@Service
@Transactional
class BookStockSynchronizationService(
  private val bookChunkProcessor: BookChunkProcessor,
  @Value("\${synchronization.book.import.chunkSize}") private val chunkSize: Int
) : BookSynchronizationService {

  private val coroutineScope = CoroutineScope(Dispatchers.Default)

  @PreDestroy
  fun cleanup() {
    coroutineScope.cancel()
  }

  /**
   * Synchronize books from import book table to book table by parsing import book content to list of BookImportDTO.
   * After parsing, process books in chunks and save them to database. Finally, delete all import books.
   */
  override fun synchronizeBooks(importedBooks: List<BookImportDTO>) {
    coroutineScope.launch {
      if (importedBooks.isEmpty()) {
        log.debug { "No import books to synchronize" }
        return@launch
      }

      processBooks(importedBooks)
    }
  }

  private fun processBooks(importedBooks: List<BookImportDTO>) {
    log.debug { "Synchronizing import book with size: ${importedBooks.size}" }

    importedBooks.chunked(chunkSize).forEach { bookChunk ->
      bookChunkProcessor.deactivateExistingBooks(bookChunk)
    }

    importedBooks.chunked(chunkSize).forEach { bookChunk ->
      bookChunkProcessor.processBookChunk(bookChunk)
    }
  }
}
