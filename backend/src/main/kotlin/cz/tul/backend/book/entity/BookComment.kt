package cz.tul.backend.book.entity

import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.book.dto.BookCommentCreateDTO
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class BookComment(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @ManyToOne(optional = false)
  val book: Book,
  @ManyToOne(optional = false)
  val authUser: AuthUser,
  val comment: String,
  val createdDateTime: LocalDateTime = LocalDateTime.now()
) {

  companion object {
    fun from(book: Book, authUser: AuthUser, createDTO: BookCommentCreateDTO): BookComment {
      return BookComment(
        book = book,
        authUser = authUser,
        comment = createDTO.comment
      )
    }
  }
}
