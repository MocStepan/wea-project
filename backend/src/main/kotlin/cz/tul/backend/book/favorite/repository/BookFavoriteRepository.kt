package cz.tul.backend.book.favorite.repository

import cz.tul.backend.book.entity.BookFavorite
import org.springframework.data.jpa.repository.JpaRepository

interface BookFavoriteRepository : JpaRepository<BookFavorite, Long> {

  fun findByAuthUser_IdAndBook_Id(authUserId: Long, bookId: Long): BookFavorite?

  fun existsByAuthUser_IdAndBook_Id(authUserId: Long, bookId: Long): Boolean
}
