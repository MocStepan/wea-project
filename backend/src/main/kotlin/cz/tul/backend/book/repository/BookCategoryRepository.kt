package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.BookCategory
import org.springframework.data.jpa.repository.JpaRepository

interface BookCategoryRepository : JpaRepository<BookCategory, Long> {

  fun findByName(name: String): BookCategory?
}
