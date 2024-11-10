package cz.tul.backend.book.service

import cz.tul.backend.auth.base.dto.AuthJwtClaims
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.repository.AuthUserRepository
import cz.tul.backend.book.dto.BookRatingCreateDTO
import cz.tul.backend.book.dto.BookRatingDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookRating
import cz.tul.backend.book.repository.BookRatingRepository
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
  private val authUserRepository: AuthUserRepository
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
    val (book, authUser) = getBookAndAuthUser(id, claims) ?: return false
    changeBookRating(createDTO.rating, 1, book)
    createOrUpdateBookRating(authUser, book, createDTO.rating)
    return true
  }

  fun deleteBookRating(id: Long, claims: AuthJwtClaims): Boolean {
    val (book, authUser) = getBookAndAuthUser(id, claims) ?: return false
    val bookRating = bookRatingRepository.findByAuthUser_IdAndBook_Id(authUser.id, book.id)
    if (bookRating == null) {
      log.warn { "BookRating for book with id $id and authUser with id ${claims.authUserId} not found" }
      return false
    }

    changeBookRating(-bookRating.rating, -1, book)
    bookRatingRepository.delete(bookRating)
    return true
  }

  private fun getBookAndAuthUser(id: Long, claims: AuthJwtClaims): Pair<Book, AuthUser>? {
    val authUser = authUserRepository.findByIdOrNull(claims.authUserId)
    if (authUser == null) {
      log.warn { "AuthUser with id ${claims.authUserId} not found" }
      return null
    }

    val book = bookRepository.findByIdOrNull(id)
    if (book == null) {
      log.warn { "Book with id $id not found" }
      return null
    }

    return Pair(book, authUser)
  }

  private fun createOrUpdateBookRating(authUser: AuthUser, book: Book, rating: Double) {
    val bookRating = bookRatingRepository.findByAuthUser_IdAndBook_Id(authUser.id, book.id)
    if (bookRating != null) {
      bookRating.rating = rating
      bookRatingRepository.save(bookRating)
    } else {
      bookRatingRepository.save(BookRating.from(authUser, book, rating))
    }
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
