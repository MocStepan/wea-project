package cz.tul.backend.book.service

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.book.dto.BookAuthorOptionView
import cz.tul.backend.book.dto.BookCategoryOptionView
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.BookImport
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryRepository
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
class BookService(
  private val objectMapper: ObjectMapper,
  private val bookImportRepository: BookImportRepository,
  private val bookCategoryRepository: BookCategoryRepository,
  private val bookAuthorRepository: BookAuthorRepository
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

  /**
   * Get all categories from database and return them as set of [BookCategoryOptionView].
   *
   * @return set of [BookCategoryOptionView]
   */
  fun getAllCategories(): Set<BookCategoryOptionView> {
    return bookCategoryRepository.findAll().map {
      BookCategoryOptionView(it.name)
    }.toSet()
  }

  /**
   * Get all authors from database and return them as set of [BookAuthorOptionView].
   *
   * @return set of [BookAuthorOptionView]
   */
  fun getAllAuthors(): Set<BookAuthorOptionView> {
    return bookAuthorRepository.findAll().map {
      BookAuthorOptionView(it.name)
    }.toSet()
  }
}
