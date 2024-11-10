package cz.tul.backend.book.rating.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BookRatingCreateDTO(
  @JsonProperty("rating")
  val rating: Double
)
