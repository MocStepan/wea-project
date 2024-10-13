package cz.tul.backend.utils

import cz.tul.backend.book.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IntegrationTestService(
  private val bookRepository: BookRepository
) {

  fun cleanDatabase() {
    bookRepository.deleteAll()
  }
}
