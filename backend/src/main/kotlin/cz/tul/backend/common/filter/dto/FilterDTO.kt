package cz.tul.backend.common.filter.dto

interface FilterDTO {
  val size: Int
  val page: Int

  fun getStartIndex(): Int {
    return page * size
  }

  fun toFilterCriteria(): MutableList<FilterCriteria>
}
