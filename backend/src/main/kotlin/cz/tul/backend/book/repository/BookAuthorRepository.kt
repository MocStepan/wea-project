package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.BookAuthor
import org.springframework.data.jpa.repository.JpaRepository

interface BookAuthorRepository : JpaRepository<BookAuthor, Long> {

  fun existsByBook_IdAndName(id: Long, name: String): Boolean
}
