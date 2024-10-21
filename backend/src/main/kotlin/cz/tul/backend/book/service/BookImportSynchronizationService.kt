package cz.tul.backend.book.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookAuthorLink
import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.entity.BookCategoryLink
import cz.tul.backend.book.repository.BookAuthorLinkRepository
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryLinkRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.repository.BookImportRepository
import cz.tul.backend.book.repository.BookRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

private val log = KotlinLogging.logger {}

/**
 * Service for synchronizing books from import table to book table.
 */
@Service
class BookImportSynchronizationService(
  private val bookImportRepository: BookImportRepository,
  private val bookRepository: BookRepository,
  private val bookAuthorRepository: BookAuthorRepository,
  private val bookCategoryRepository: BookCategoryRepository,
  private val bookCategoryLinkRepository: BookCategoryLinkRepository,
  private val bookAuthorLinkRepository: BookAuthorLinkRepository,
  private val objectMapper: ObjectMapper,
  @Value("\${synchronization.book.import.chunkSize}") private val chunkSize: Int,
  private val transactionTemplate: TransactionTemplate
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

    transactionTemplate.executeWithoutResult {
      bookRepository.deleteAll()
      bookAuthorRepository.deleteAll()
      bookCategoryRepository.deleteAll()
    }

    importBooks.forEach { importBook ->
      log.debug { "Synchronizing import book: ${importBook.id}" }
      val books = parseBooks(importBook.content)
      if (books == null) {
        log.warn { "Failed to parse import book content" }
        return
      }

      processBooksInBatch(books)
    }
    bookImportRepository.deleteAll()
  }

  /**
   * Process chunks of import books with transaction.
   * Save books, authors and categories to database.
   *
   * @param books list of books to process
   */
  private fun processBooksInBatch(books: List<BookImportDTO>) {
    books.chunked(chunkSize).forEach { bookChunk ->
      transactionTemplate.executeWithoutResult {
        bookChunk.forEach { importDTO ->
          val book = bookRepository.save(Book.from(importDTO))
          saveBookCategories(importDTO.categories, book)
          saveBookAuthors(importDTO.authors, book)
        }
      }
    }
  }

  /**
   * Save book categories to database. If category does not exist, create new category and link it to book.
   *
   * @param categories string with categories separated by comma
   * @param book book to save categories for
   */
  private fun saveBookCategories(categories: String?, book: Book) {
    if (categories == null) {
      return
    }

    categories.getCategories().forEach { category ->
      bookCategoryLinkRepository.existsByBook_IdAndCategory_Name(book.id, category).takeIf { !it }?.let {
        val bookCategory = bookCategoryRepository.findByName(category)

        val newBookCategory = bookCategory ?: bookCategoryRepository.save(BookCategory(name = category))
        bookCategoryLinkRepository.save(BookCategoryLink(book = book, category = newBookCategory))
      }
    }
  }

  /**
   * Save book authors to database. If author does not exist, create new author and link it to book.
   *
   * @param authors string with authors separated by semicolon
   * @param book book to save authors for
   */
  private fun saveBookAuthors(authors: String?, book: Book) {
    if (authors == null) {
      return
    }

    authors.getAuthors().forEach { author ->
      bookAuthorLinkRepository.existsByBook_IdAndAuthor_Name(book.id, author).takeIf { !it }?.let {
        val bookAuthor = bookAuthorRepository.findByName(author)

        val newBookAuthor = bookAuthor ?: bookAuthorRepository.save(BookAuthor(name = author))
        bookAuthorLinkRepository.save(BookAuthorLink(book = book, author = newBookAuthor))
      }
    }
  }

  /**
   * Special method that can be used by string to get list of categories from string separated by comma.
   *
   * @return list of categories
   */
  private fun String.getCategories(): List<String> {
    return this.split(",").map { it.trim() }
  }

  /**
   * Special method that can be used by string to get list of authors from string separated by semicolon.
   *
   * @return list of authors
   */
  private fun String.getAuthors(): List<String> {
    return this.split(";").map { it.trim() }
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
