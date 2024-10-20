package cz.tul.backend.common.filter.blaze

import com.blazebit.persistence.PaginatedCriteriaBuilder
import cz.tul.backend.common.filter.valueobject.FilterSort
import org.springframework.stereotype.Component

/**
 * Builder for sorting criteria.
 */
@Component
class BlazeSortCriteriaBuilder {

  /**
   * Build sorting criteria.
   *
   * @param criteriaBuilder Criteria builder.
   * @param key key of entity.
   * @param sort sort type (ASC, DESC) or null.
   */
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
