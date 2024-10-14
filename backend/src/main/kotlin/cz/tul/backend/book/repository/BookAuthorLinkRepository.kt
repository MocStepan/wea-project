package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.BookAuthorLink
import org.springframework.data.jpa.repository.JpaRepository

interface BookAuthorLinkRepository : JpaRepository<BookAuthorLink, Long> {

  fun existsByBook_IdAndAuthor_Name(id: Long, name: String): Boolean
}
