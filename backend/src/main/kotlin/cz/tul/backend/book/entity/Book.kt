package cz.tul.backend.book.entity

import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.cart.entity.CartItem
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Book(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  val isbn13: String,
  val isbn10: String,
  var title: String,
  @OneToMany(mappedBy = "book", orphanRemoval = true)
  val categories: Set<BookCategoryLink> = mutableSetOf(),
  var subtitle: String? = null,
  @OneToMany(mappedBy = "book", orphanRemoval = true)
  val authors: Set<BookAuthorLink> = mutableSetOf(),
  @OneToMany(mappedBy = "book", orphanRemoval = true)
  val bookComments: Set<BookComment> = mutableSetOf(),
  var thumbnail: String? = null,
  var description: String? = null,
  var publishedYear: Int? = null,
  var averageRating: Double? = null,
  var numPages: Int? = null,
  var ratingsCount: Int? = null,
  var disabled: Boolean = false,
  @OneToMany(mappedBy = "book", orphanRemoval = true)
  val bookRatings: Set<BookRating> = mutableSetOf(),
  @OneToMany(mappedBy = "book", orphanRemoval = true)
  val favorites: Set<BookFavorite> = mutableSetOf(),
  var price: Double,
  @OneToMany(mappedBy = "book", orphanRemoval = true)
  val cartItem: List<CartItem> = mutableListOf()
) {

  companion object {
    fun from(importDTO: BookImportDTO): Book {
      return Book(
        isbn13 = importDTO.isbn13,
        isbn10 = importDTO.isbn10,
        title = importDTO.title,
        subtitle = importDTO.subtitle,
        thumbnail = importDTO.thumbnail,
        description = importDTO.description,
        publishedYear = importDTO.publishedYear,
        averageRating = importDTO.averageRating,
        numPages = importDTO.numPages,
        ratingsCount = importDTO.ratingsCount,
        price = importDTO.price
      )
    }
  }

  fun update(importDTO: BookImportDTO) {
    title = importDTO.title
    subtitle = importDTO.subtitle
    thumbnail = importDTO.thumbnail
    description = importDTO.description
    publishedYear = importDTO.publishedYear
    numPages = importDTO.numPages
    price = importDTO.price
    disabled = false
  }

  fun updateRating(rating: Double, count: Int) {
    averageRating = rating
    ratingsCount = count
  }
}
