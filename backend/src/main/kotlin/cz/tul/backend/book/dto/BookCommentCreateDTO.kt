package cz.tul.backend.book.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BookCommentCreateDTO(
  @JsonProperty("comment")
  val comment: String
) {

  fun isValid(): Boolean {
    return comment.isNotBlank()
  }
}
