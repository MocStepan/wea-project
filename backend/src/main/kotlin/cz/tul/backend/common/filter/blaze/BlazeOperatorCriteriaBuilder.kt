package cz.tul.backend.common.filter.blaze

import com.blazebit.persistence.PaginatedCriteriaBuilder
import cz.tul.backend.common.filter.valueobject.FilterOperator
import org.springframework.stereotype.Component

@Component
class BlazeOperatorCriteriaBuilder {

  fun <T> buildOperatorCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: String?,
    operator: FilterOperator?
  ) {
    when (operator) {
      null -> {}
      FilterOperator.EQUAL -> createEqualCriteria(criteriaBuilder, joinedKeys, value)
    }
  }

  private fun <T> createEqualCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: String?
  ) {
    if (value == null) {
      criteriaBuilder.where(joinedKeys).isNull
    } else {
      criteriaBuilder.where(joinedKeys).eq(value)
    }
  }
}
