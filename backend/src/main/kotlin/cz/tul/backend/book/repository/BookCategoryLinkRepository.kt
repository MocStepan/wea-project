package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.BookCategoryLink
import org.springframework.data.jpa.repository.JpaRepository

interface BookCategoryLinkRepository : JpaRepository<BookCategoryLink, Long> {

  fun existsByBook_IdAndCategory_Name(id: Long, name: String): Boolean
}
