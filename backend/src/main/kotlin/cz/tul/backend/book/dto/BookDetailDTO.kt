package cz.tul.backend.book.dto

import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.entity.BookComment

data class BookDetailDTO(
  val isbn13: String,
  val isbn10: String,
  val title: String,
  val categories: List<BookCategoryDetailDTO>,
  val subtitle: String?,
  val authors: List<BookAuthorDetailDTO>,
  val thumbnail: String?,
  val description: String?,
  val publishedYear: Int?,
  val averageRating: Double?,
  val numPages: Int?,
  val ratingsCount: Int?,
  val bookComments: List<BookCommentDetailDTO>,
  val disabled: Boolean,
  val favorite: Boolean,
  val price: Double
) {

  companion object {
    fun from(
      book: Book,
      categories: Set<BookCategory>,
      authors: Set<BookAuthor>,
      comments: Set<BookComment>,
      favorite: Boolean
    ): BookDetailDTO {
      return BookDetailDTO(
        isbn13 = book.isbn13,
        isbn10 = book.isbn10,
        title = book.title,
        categories = categories.map { BookCategoryDetailDTO.from(it) },
        subtitle = book.subtitle,
        authors = authors.map { BookAuthorDetailDTO.from(it) },
        thumbnail = book.thumbnail,
        description = book.description,
        publishedYear = book.publishedYear,
        averageRating = book.averageRating,
        numPages = book.numPages,
        ratingsCount = book.ratingsCount,
        bookComments = comments.map { BookCommentDetailDTO.from(it) },
        disabled = book.disabled,
        favorite = favorite,
        price = book.price
      )
    }
  }
}
