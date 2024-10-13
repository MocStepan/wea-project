package cz.tul.backend.book.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.repository.BookImportRepository
import cz.tul.backend.book.repository.BookRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class BookImportSynchronizationService(
  private val bookImportRepository: BookImportRepository,
  private val bookRepository: BookRepository,
  private val bookAuthorRepository: BookAuthorRepository,
  private val bookCategoryRepository: BookCategoryRepository,
  private val objectMapper: ObjectMapper
) : BookSynchronizationService {

  override fun synchronizeBooks() {
    val importBooks = bookImportRepository.findAll()
    if (importBooks.isEmpty()) {
      log.warn { "No books to synchronize" }
      return
    }

    importBooks.forEach { importBook ->
      try {
        log.debug { "Synchronizing book: ${importBook.id}" }
        val books = parseBooks(importBook.content)

        books.forEach {
          processBook(it)
        }
      } catch (e: Exception) {
        log.error(e) { "Error synchronizing book: ${importBook.id}" }
      }
    }
    bookImportRepository.deleteAll()
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

    categories.getCategories().forEach {
      val exists = bookCategoryRepository.existsByBook_IdAndName(book.id, it)
      if (!exists) {
        bookCategoryRepository.save(BookCategory(book = book, name = it))
      }
    }
  }

  private fun saveBookAuthors(categories: String?, book: Book) {
    if (categories == null) {
      return
    }

    categories.getAuthors().forEach {
      val exists = bookAuthorRepository.existsByBook_IdAndName(book.id, it)
      if (!exists) {
        bookAuthorRepository.save(BookAuthor(book = book, name = it))
      }
    }
  }

  private fun String.getCategories(): List<String> {
    return this.split(",").map { it.trim() }
  }

  private fun String.getAuthors(): List<String> {
    return this.split(";").map { it.trim() }
  }

  private fun parseBooks(content: String): List<BookImportDTO> {
    return objectMapper.readValue(content, object : TypeReference<List<BookImportDTO>>() {})
  }
}
