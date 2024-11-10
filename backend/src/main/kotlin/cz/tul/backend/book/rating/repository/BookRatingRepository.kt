package cz.tul.backend.book.rating.repository

import cz.tul.backend.book.entity.BookRating
import org.springframework.data.jpa.repository.JpaRepository

interface BookRatingRepository : JpaRepository<BookRating, Long> {

  fun findByAuthUser_IdAndBook_Id(authUserId: Long, bookId: Long): BookRating?

  fun existsByAuthUser_IdAndBook_Id(authUserId: Long, bookId: Long): Boolean
}
