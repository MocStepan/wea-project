package cz.tul.backend.book.dto

import cz.tul.backend.book.entity.BookAuthor

data class BookAuthorDetailDTO(
  val name: String
) {
  companion object {
    fun from(author: BookAuthor): BookAuthorDetailDTO {
      return BookAuthorDetailDTO(
        name = author.name
      )
    }
  }
}
