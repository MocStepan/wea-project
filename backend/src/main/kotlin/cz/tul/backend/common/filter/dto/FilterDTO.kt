package cz.tul.backend.common.filter.dto

import com.fasterxml.jackson.databind.ObjectMapper

interface FilterDTO {
  val size: Int
  val page: Int

  /**
   * Returns the start index of the page.
   */
  fun getStartIndex(): Int {
    return page * size
  }

  /**
   * Extracts the filter criteria from the filter DTO.
   */
  fun toFilterCriteria(objectMapper: ObjectMapper): MutableList<FilterCriteria<Any>>
}
