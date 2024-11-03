package cz.tul.backend.book.dto

import cz.tul.backend.book.entity.BookComment
import java.time.LocalDateTime

data class BookCommentDetailDTO(
  val comment: String,
  val createdDateTime: LocalDateTime
) {
  companion object {
    fun from(comment: BookComment): BookCommentDetailDTO {
      return BookCommentDetailDTO(
        comment = comment.comment,
        createdDateTime = comment.createdDateTime
      )
    }
  }
}
