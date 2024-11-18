package cz.tul.backend.book.service

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.service.AuthUserService
import cz.tul.backend.book.dto.BookCommentCreateDTO
import cz.tul.backend.book.entity.BookComment
import cz.tul.backend.book.repository.BookCommentRepository
import cz.tul.backend.book.repository.BookRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class BookCommentService(
  private val bookCommentRepository: BookCommentRepository,
  private val bookRepository: BookRepository,
  private val authUserService: AuthUserService
) {

  /**
   * Create a book comment. Returns true if the comment was created successfully, false otherwise.
   *
   * @param bookId the id of the book to comment on
   * @param createDTO the DTO containing the comment text
   * @param claims the JWT claims of the authenticated user
   * @return true if the comment was created successfully, false otherwise
   * @see BookCommentCreateDTO
   */
  fun createBookComment(bookId: Long, createDTO: BookCommentCreateDTO, claims: AuthJwtClaims): Boolean {
    if (!createDTO.isValid()) {
      log.warn { "BookCommentCreateDTO: $createDTO is not valid" }
      return false
    }

    val authUser = authUserService.getReferenceIfExists(claims.authUserId)
    if (authUser == null) {
      log.warn { "AuthUser with id ${claims.authUserId} not found" }
      return false
    }

    val book = bookRepository.findByIdOrNull(bookId)
    if (book == null || book.disabled) {
      log.warn { "Book with id $bookId not found or book is disabled" }
      return false
    }

    BookComment.from(book, authUser, createDTO).let {
      bookCommentRepository.save(it)
    }
    return true
  }
}
