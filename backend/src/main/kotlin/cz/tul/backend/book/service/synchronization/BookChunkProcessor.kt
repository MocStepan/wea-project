package cz.tul.backend.book.service.synchronization

import cz.tul.backend.audit.service.BookStockAuditService
import cz.tul.backend.audit.valueobject.AuditType
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Processor for processing book chunks and deactivating existing books.
 */
@Service
class BookChunkProcessor(
  private val bookCategoryImportComponent: BookCategoryImportComponent,
  private val bookAuthorImportComponent: BookAuthorImportComponent,
  private val bookRepository: BookRepository,
  private val bookStockAuditService: BookStockAuditService
) {

  /**
   * Process books in chunks and create new book or update existing one.
   *
   * @param bookChunk list of books to process
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  fun processBookChunk(bookChunk: List<BookImportDTO>) {
    bookChunk.forEach { importDTO ->
      val book = bookRepository.findByIsbn13(importDTO.isbn13).let {
        it.createOrUpdate(importDTO)
      }

      bookCategoryImportComponent.processBookCategories(importDTO.categories, book)
      bookAuthorImportComponent.processBookAuthors(importDTO.authors, book)
    }
  }

  /**
   * Create new book or update existing one.
   *
   * @param importDTO book import DTO
   * @return created or updated book
   */
  private fun Book?.createOrUpdate(importDTO: BookImportDTO): Book {
    if (this == null) {
      saveAuditLog(AuditType.CBD_NEW_BOOK, importDTO.isbn13)
      return bookRepository.save(Book.from(importDTO))
    }

    if (this.disabled) {
      saveAuditLog(AuditType.CBD_SHOW_BOOK, importDTO.isbn13)
    }

    this.update(importDTO)
    return bookRepository.save(this)
  }

  /**
   * Deactivate existing books that are not in imported books.
   *
   * @param importedBooks list of imported books
   */
  fun deactivateExistingBooks(importedBooks: List<BookImportDTO>) {
    val isbn13s = importedBooks.map { it.isbn13 }
    bookRepository.findByIsbn13NotIn(isbn13s).forEach {
      if (it.disabled) {
        return@forEach
      }

      saveAuditLog(AuditType.CBD_HIDE_BOOK, it.isbn13)
      it.disabled = true
      bookRepository.save(it)
    }
  }

  /**
   * Save audit log for book stock.
   *
   * @param auditType type of audit
   * @param isbn13 ISBN-13 of book
   */
  private fun saveAuditLog(auditType: AuditType, isbn13: String) {
    bookStockAuditService.saveAuditLog(auditType, "System", isbn13)
  }
}
