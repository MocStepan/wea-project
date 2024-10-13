package cz.tul.backend.common.filter.dto

import cz.tul.backend.common.filter.valueobject.FilterOperator
import cz.tul.backend.common.filter.valueobject.FilterSort

data class FilterCriteriaDTO(
  val operator: FilterOperator? = null,
  val value: String? = null,
  val sort: FilterSort? = null
)
