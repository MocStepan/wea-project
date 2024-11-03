package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.BookAuthor
import org.springframework.data.jpa.repository.JpaRepository

interface BookAuthorRepository : JpaRepository<BookAuthor, Long> {

  fun findByName(name: String): BookAuthor?

  fun findByBookAuthorLink_Book_Id(id: Long): Set<BookAuthor>
}
