package cz.tul.backend.book.rating.service

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.service.AuthUserService
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookRating
import cz.tul.backend.book.rating.dto.BookRatingCreateDTO
import cz.tul.backend.book.rating.dto.BookRatingDTO
import cz.tul.backend.book.rating.repository.BookRatingRepository
import cz.tul.backend.book.repository.BookRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class BookRatingService(
  private val bookRepository: BookRepository,
  private val bookRatingRepository: BookRatingRepository,
  private val authUserService: AuthUserService
) {

  fun getBookRating(id: Long, claims: AuthJwtClaims): BookRatingDTO? {
    val bookRating = bookRatingRepository.findByAuthUser_IdAndBook_Id(claims.authUserId, id)
    if (bookRating == null) {
      log.warn { "BookRating for book with id $id and authUser with id ${claims.authUserId} not found" }
      return null
    }

    return BookRatingDTO(bookRating.rating)
  }

  fun createBookRating(id: Long, createDTO: BookRatingCreateDTO, claims: AuthJwtClaims): Boolean {
    val existsBookRating = bookRatingRepository.existsByAuthUser_IdAndBook_Id(claims.authUserId, id)
    if (existsBookRating) {
      log.warn { "BookRating for book with id $id and authUser with id ${claims.authUserId} not found" }
      return false
    }

    val authUser = authUserService.getReferenceIfExists(claims.authUserId)
    if (authUser == null) {
      log.warn { "AuthUser with id ${claims.authUserId} not found" }
      return false
    }

    val book = bookRepository.findByIdOrNull(id)
    if (book == null || book.disabled) {
      log.warn { "Book with id $id not found or book is disabled" }
      return false
    }

    changeBookRating(createDTO.rating, 1, book)
    bookRatingRepository.save(BookRating.from(authUser, book, createDTO.rating))
    return true
  }

  fun editBookRating(id: Long, updateDTO: BookRatingCreateDTO, claims: AuthJwtClaims): Boolean {
    val (book, bookRating) = getBookAndBookRating(id, claims) ?: return false

    changeBookRating(updateDTO.rating - bookRating.rating, 0, book)
    bookRating.rating = updateDTO.rating
    bookRatingRepository.save(bookRating)
    return true
  }

  fun deleteBookRating(id: Long, claims: AuthJwtClaims): Boolean {
    val (book, bookRating) = getBookAndBookRating(id, claims) ?: return false

    changeBookRating(-bookRating.rating, -1, book)
    bookRatingRepository.delete(bookRating)
    return true
  }

  private fun getBookAndBookRating(id: Long, claims: AuthJwtClaims): Pair<Book, BookRating>? {
    val bookRating = bookRatingRepository.findByAuthUser_IdAndBook_Id(claims.authUserId, id)
    if (bookRating == null) {
      log.warn { "BookRating for book with id $id and authUser with id ${claims.authUserId} not found" }
      return null
    }

    val book = bookRepository.findByIdOrNull(id)
    if (book == null || book.disabled) {
      log.warn { "Book with id $id not found or book is disabled" }
      return null
    }

    return Pair(book, bookRating)
  }

  private fun changeBookRating(rating: Double, count: Int, book: Book) {
    val averageRating = book.averageRating ?: 0.0
    val ratingsCount = book.ratingsCount ?: 0

    val newRatingCount = ratingsCount + count
    val newAverageRating = (averageRating * ratingsCount + rating)
    if (newRatingCount == 0) {
      book.updateRating(0.0, 0)
    } else {
      book.updateRating(newAverageRating / newRatingCount, newRatingCount)
    }

    bookRepository.save(book)
  }
}
