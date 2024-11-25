package cz.tul.backend.book.dto

data class BookImportDTO(
  val isbn13: String,
  val isbn10: String,
  val title: String,
  val categories: String?,
  val subtitle: String?,
  val authors: String?,
  val thumbnail: String?,
  val description: String?,
  val publishedYear: Int?,
  val averageRating: Double?,
  val numPages: Int?,
  val ratingsCount: Int?,
  val price: Double
)
