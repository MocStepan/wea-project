package cz.tul.backend.book.favorite.service

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.service.AuthUserService
import cz.tul.backend.book.entity.BookFavorite
import cz.tul.backend.book.favorite.repository.BookFavoriteRepository
import cz.tul.backend.book.repository.BookRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Service for handling book favorites.
 */
@Service
@Transactional
class BookFavoriteService(
  private val bookRepository: BookRepository,
  private val bookFavoriteRepository: BookFavoriteRepository,
  private val authUserService: AuthUserService
) {

  fun addBookToFavorite(bookId: Long, claims: AuthJwtClaims): Boolean {
    val exists = bookFavoriteRepository.existsByAuthUser_IdAndBook_Id(claims.authUserId, bookId)
    if (exists) {
      log.warn { "Book with id $bookId is already in favorites" }
      return false
    }

    val authUser = authUserService.getReferenceIfExists(claims.authUserId)
    if (authUser == null) {
      log.warn { "AuthUser with id ${claims.authUserId} not found" }
      return false
    }

    val book = bookRepository.findByIdOrNull(bookId)
    if (book == null || book.disabled) {
      log.warn { "Book with id $bookId not found" }
      return false
    }

    val favorite = BookFavorite(book = book, authUser = authUser)
    bookFavoriteRepository.save(favorite)
    return true
  }

  fun deleteBookFromFavorite(id: Long, claims: AuthJwtClaims): Boolean {
    val bookFavorite = bookFavoriteRepository.findByAuthUser_IdAndBook_Id(claims.authUserId, id)
    if (bookFavorite == null) {
      log.warn { "Book with id $id is not in favorites" }
      return false
    }

    bookFavoriteRepository.delete(bookFavorite)
    return true
  }
}
