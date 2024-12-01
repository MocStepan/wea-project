package cz.tul.backend.book.service

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.book.dto.BookAuthorOptionView
import cz.tul.backend.book.dto.BookCategoryOptionView
import cz.tul.backend.book.dto.BookDetailDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.favorite.repository.BookFavoriteRepository
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.repository.BookCommentRepository
import cz.tul.backend.book.repository.BookRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

/**
 * Service for handling books.
 */
@Service
class BookService(
  private val bookRepository: BookRepository,
  private val bookCommentRepository: BookCommentRepository,
  private val bookCategoryRepository: BookCategoryRepository,
  private val bookAuthorRepository: BookAuthorRepository,
  private val bookFavoriteRepository: BookFavoriteRepository
) {

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

  /**
   * Get detail of a book with given id. Return null if book with given id does not exist.
   *
   * @param id id of the book
   * @return BookDetailDTO or null
   * @see BookDetailDTO
   */
  fun getBookDetail(id: Long, claims: AuthJwtClaims?): BookDetailDTO? {
    val book = bookRepository.findByIdOrNull(id)
    if (book == null) {
      log.warn { "Book with id $id not found" }
      return null
    }

    val categories = bookCategoryRepository.findByBookCategoryLink_Book_Id(id)
    val authors = bookAuthorRepository.findByBookAuthorLink_Book_Id(id)
    val comments = bookCommentRepository.findByBook_Id(id)
    val isFavorite = claims?.let { bookFavoriteRepository.existsByAuthUser_IdAndBook_Id(it.authUserId, id) } ?: false

    return BookDetailDTO.from(book, categories, authors, comments, isFavorite)
  }

  fun getReferenceIfExists(id: Long): Book? {
    return bookRepository.findByIdOrNull(id)
  }
}
