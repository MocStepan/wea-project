package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.BookRating
import org.springframework.data.jpa.repository.JpaRepository

interface BookRatingRepository : JpaRepository<BookRating, Long> {

  fun findByAuthUser_IdAndBook_Id(id: Long, id1: Long): BookRating?
}
