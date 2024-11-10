package cz.tul.backend.book.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BookRatingCreateDTO(
  @JsonProperty("rating")
  val rating: Double
)
