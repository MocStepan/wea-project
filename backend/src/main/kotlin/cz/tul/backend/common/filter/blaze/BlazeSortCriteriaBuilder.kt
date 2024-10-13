package cz.tul.backend.common.filter.blaze

import com.blazebit.persistence.PaginatedCriteriaBuilder
import cz.tul.backend.common.filter.valueobject.FilterSort
import org.springframework.stereotype.Component

@Component
class BlazeSortCriteriaBuilder {

  fun <T> buildOperatorCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    key: String,
    sort: FilterSort?
  ) {
    when (sort) {
      null -> {}
      FilterSort.ASC -> criteriaBuilder.orderByAsc(key)
      FilterSort.DESC -> criteriaBuilder.orderByDesc(key)
    }
  }
}
