package cz.tul.backend.personinfo.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PersonInfoCategoryDTO(
  @JsonProperty("name")
  val name: String
)
