package cz.tul.backend.book.dto

import cz.tul.backend.book.entity.BookCategory

data class BookCategoryDetailDTO(
  val name: String
) {
  companion object {
    fun from(category: BookCategory): BookCategoryDetailDTO {
      return BookCategoryDetailDTO(
        name = category.name
      )
    }
  }
}
