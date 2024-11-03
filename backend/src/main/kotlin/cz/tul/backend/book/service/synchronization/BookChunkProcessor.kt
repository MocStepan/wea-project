package cz.tul.backend.book.service.synchronization

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
  private val bookRepository: BookRepository
) {

  /**
   * Process books in chunks and create new book or update existing one.
   *
   * @param bookChunk list of books to process
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  fun processBookChunk(bookChunk: List<BookImportDTO>) {
    bookChunk.forEach { importDTO ->
      val book = bookRepository.findByIsbn13(importDTO.isbn13)?.apply {
        this.update(importDTO)
        bookRepository.save(this)
      } ?: bookRepository.save(Book.from(importDTO))

      bookCategoryImportComponent.processBookCategories(importDTO.categories, book)
      bookAuthorImportComponent.processBookAuthors(importDTO.authors, book)
    }
  }

  /**
   * Deactivate existing books that are not in imported books.
   *
   * @param importedBooks list of imported books
   */
  fun deactivateExistingBooks(importedBooks: List<BookImportDTO>) {
    val isbn13s = importedBooks.map { it.isbn13 }
    bookRepository.findByIsbn13NotIn(isbn13s).forEach {
      it.disabled = true
      bookRepository.save(it)
    }
  }
}
