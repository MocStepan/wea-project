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

  override fun synchronizeBooks() {
    val importBooks = bookImportRepository.findAll()
    if (importBooks.isEmpty()) {
      log.warn { "No import books to synchronize" }
      return
    }

    importBooks.forEach { importBook ->
      log.debug { "Synchronizing import book: ${importBook.id}" }
      val books = parseBooks(importBook.content) ?: return
      processBooksInBatch(books)
    }
    bookImportRepository.deleteAll()
  }

  private fun processBooksInBatch(books: List<BookImportDTO>) {
    books.chunked(chunkSize).forEach { bookChunk ->
      transactionTemplate.executeWithoutResult {
        bookChunk.forEach { processBook(it) }
      }
    }
  }

  private fun processBook(importDTO: BookImportDTO) {
    try {
      val book = bookRepository.save(Book.from(importDTO))
      saveBookCategories(importDTO.categories, book)
      saveBookAuthors(importDTO.authors, book)
    } catch (e: Exception) {
      log.error(e) { "Error synchronizing bookImportDTO: $importDTO" }
    }
  }

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

  private fun String.getCategories(): List<String> {
    return this.split(",").map { it.trim() }
  }

  private fun String.getAuthors(): List<String> {
    return this.split(";").map { it.trim() }
  }

  private fun parseBooks(content: ByteArray): List<BookImportDTO>? {
    return try {
      objectMapper.readValue(content, object : TypeReference<List<BookImportDTO>>() {})
    } catch (e: Exception) {
      log.error(e) { "Error parsing import book content to list of BookImportDTO" }
      null
    }
  }
}
