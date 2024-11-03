package cz.tul.backend.book.service.synchronization

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.repository.BookImportRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Service for synchronizing books from import table to book table.
 */
@Service
@Transactional
class BookImportSynchronizationService(
  private val bookChunkProcessor: BookChunkProcessor,
  private val bookImportRepository: BookImportRepository,
  private val objectMapper: ObjectMapper,
  @Value("\${synchronization.book.import.chunkSize}") private val chunkSize: Int
) : BookSynchronizationService {

  /**
   * Synchronize books from import book table to book table by parsing import book content to list of BookImportDTO.
   * After parsing, process books in chunks and save them to database. Finally, delete all import books.
   */
  override fun synchronizeBooks() {
    val importBooks = bookImportRepository.findAll()
    if (importBooks.isEmpty()) {
      log.debug { "No import books to synchronize" }
      return
    }

    importBooks.forEach { importBook ->
      log.debug { "Synchronizing import book: ${importBook.id}" }
      val books = parseBooks(importBook.content)
      if (books == null) {
        log.warn { "Failed to parse import book content for ID: ${importBook.id}" }
        return
      }

      bookChunkProcessor.deactivateExistingBooks(books)
      books.chunked(chunkSize).forEach { bookChunk ->
        bookChunkProcessor.processBookChunk(bookChunk)
      }
    }
    bookImportRepository.deleteAll()
  }

  /**
   * Parse import book content to list of BookImportDTO. If error occurs, log error and return null.
   *
   * @param content import book content
   * @return list of [BookImportDTO] or null if parsing failed
   */
  private fun parseBooks(content: ByteArray): List<BookImportDTO>? {
    return try {
      objectMapper.readValue(content, object : TypeReference<List<BookImportDTO>>() {})
    } catch (e: Exception) {
      log.error(e) { "Error parsing import book content to list of BookImportDTO" }
      null
    }
  }
}
