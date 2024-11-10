package cz.tul.backend.book.entity

import cz.tul.backend.auth.entity.AuthUser
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class BookRating(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @ManyToOne(optional = false)
  val authUser: AuthUser,
  @ManyToOne(optional = false)
  val book: Book,
  var rating: Double
) {
  companion object {
    fun from(authUser: AuthUser, book: Book, rating: Double): BookRating {
      return BookRating(
        authUser = authUser,
        book = book,
        rating = rating
      )
    }
  }
}
