package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.BookComment
import org.springframework.data.jpa.repository.JpaRepository

interface BookCommentRepository : JpaRepository<BookComment, Long> {

  fun findByBook_Id(id: Long): Set<BookComment>
}
