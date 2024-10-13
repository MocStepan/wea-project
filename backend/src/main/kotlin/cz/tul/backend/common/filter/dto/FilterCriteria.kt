package cz.tul.backend.common.filter.dto

import cz.tul.backend.common.filter.valueobject.FilterOperator
import cz.tul.backend.common.filter.valueobject.FilterSort

class FilterCriteria(
  val operator: FilterOperator? = null,
  val value: String? = null,
  val sort: FilterSort? = null,
  val key: String,
  val joinsKeys: List<String> = listOf()
) {

  companion object {
    fun build(
      filterCriteriaDTO: FilterCriteriaDTO,
      key: String,
      joinsKeys: List<String> = listOf()
    ): FilterCriteria {
      return FilterCriteria(
        operator = filterCriteriaDTO.operator,
        value = filterCriteriaDTO.value,
        sort = filterCriteriaDTO.sort,
        key = key,
        joinsKeys = joinsKeys
      )
    }

    fun buildOrderCriteria(key: String, filterSort: FilterSort): FilterCriteria {
      return FilterCriteria(
        key = key,
        sort = filterSort
      )
    }
  }
}
