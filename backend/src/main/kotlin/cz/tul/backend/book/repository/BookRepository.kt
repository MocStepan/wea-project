package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long> {

  fun findByIsbn13NotIn(isbn13s: List<String>): List<Book>

  fun findByIsbn13(isbn13: String): Book?
}
