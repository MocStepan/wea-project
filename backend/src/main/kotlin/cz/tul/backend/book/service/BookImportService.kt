package cz.tul.backend.book.service

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.BookImport
import cz.tul.backend.book.repository.BookImportRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Service for handling books.
 */
@Service
@Transactional
class BookImportService(
  private val objectMapper: ObjectMapper,
  private val bookImportRepository: BookImportRepository
) {

  /**
   * Save imported list of books to database. Before saving, the list is converted to byte array.
   *
   * @param importDTOs list of imported books
   */
  fun saveImportedBooks(importDTOs: List<BookImportDTO>) {
    try {
      val jsonContent = objectMapper.writeValueAsBytes(importDTOs)
      bookImportRepository.save(BookImport(content = jsonContent))
    } catch (e: Exception) {
      log.error(e) { "Error saving imported books" }
    }
  }
}
