package cz.tul.backend.utils

import cz.tul.backend.audit.repository.BookStockAuditRepository
import cz.tul.backend.auth.repository.AuthUserRepository
import cz.tul.backend.book.favorite.repository.BookFavoriteRepository
import cz.tul.backend.book.rating.repository.BookRatingRepository
import cz.tul.backend.book.repository.BookAuthorLinkRepository
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryLinkRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.repository.BookCommentRepository
import cz.tul.backend.book.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IntegrationTestService(
  private val authUserRepository: AuthUserRepository,
  private val bookRepository: BookRepository,
  private val bookAuthorRepository: BookAuthorRepository,
  private val bookAuthorLinkRepository: BookAuthorLinkRepository,
  private val bookCategoryRepository: BookCategoryRepository,
  private val bookCategoryLinkRepository: BookCategoryLinkRepository,
  private val bookCommentRepository: BookCommentRepository,
  private val bookFavoriteRepository: BookFavoriteRepository,
  private val bookRatingRepository: BookRatingRepository,
  private val bookStockAuditRepository: BookStockAuditRepository
) {

  fun cleanDatabase() {
    authUserRepository.deleteAll()
    bookRepository.deleteAll()
    bookAuthorLinkRepository.deleteAll()
    bookAuthorRepository.deleteAll()
    bookCategoryLinkRepository.deleteAll()
    bookCategoryRepository.deleteAll()
    bookCommentRepository.deleteAll()
    bookFavoriteRepository.deleteAll()
    bookRatingRepository.deleteAll()
    bookStockAuditRepository.deleteAll()
  }
}
