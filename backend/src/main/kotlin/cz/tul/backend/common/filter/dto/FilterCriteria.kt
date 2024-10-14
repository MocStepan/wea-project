package cz.tul.backend.common.filter.dto

import cz.tul.backend.common.filter.valueobject.FilterOperator
import cz.tul.backend.common.filter.valueobject.FilterSort
import cz.tul.backend.common.filter.valueobject.convertPossibleStringList

class FilterCriteria<out T>(
  val operator: FilterOperator? = null,
  val value: T? = null,
  val sort: FilterSort? = null,
  val key: String,
  val joinsKeys: List<String> = listOf()
) {

  companion object {
    fun convertAndBuild(
      filterCriteriaDTO: FilterCriteriaDTO,
      key: String,
      joinsKeys: List<String> = listOf()
    ): FilterCriteria<Any> {
      return FilterCriteria(
        operator = filterCriteriaDTO.operator,
        value = convertPossibleStringList(filterCriteriaDTO.value),
        sort = filterCriteriaDTO.sort,
        key = key,
        joinsKeys = joinsKeys
      )
    }

    fun buildOrderCriteria(key: String, filterSort: FilterSort): FilterCriteria<Any> {
      return FilterCriteria(
        key = key,
        sort = filterSort
      )
    }
  }
}
