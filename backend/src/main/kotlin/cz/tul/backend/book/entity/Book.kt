package cz.tul.backend.book.entity

import cz.tul.backend.book.dto.BookImportDTO
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Book(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,
  val isbn13: String,
  val isbn10: String,
  val title: String,
  @OneToMany(mappedBy = "book", orphanRemoval = true)
  val categories: Set<BookCategoryLink> = mutableSetOf(),
  val subtitle: String? = null,
  @OneToMany(mappedBy = "book", orphanRemoval = true)
  val authors: Set<BookAuthorLink> = mutableSetOf(),
  val thumbnail: String? = null,
  val description: String? = null,
  val publishedYear: Int? = null,
  val averageRating: Double? = null,
  val numPages: Int? = null,
  val ratingsCount: Int? = null
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
        ratingsCount = importDTO.ratingsCount
      )
    }
  }
}
