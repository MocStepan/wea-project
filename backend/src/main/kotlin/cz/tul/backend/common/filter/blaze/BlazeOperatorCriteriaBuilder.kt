package cz.tul.backend.common.filter.blaze

import com.blazebit.persistence.PaginatedCriteriaBuilder
import cz.tul.backend.common.filter.valueobject.FilterOperator
import org.springframework.stereotype.Component

@Component
class BlazeOperatorCriteriaBuilder(
  private val operatorValidatorComponent: BlazeOperatorValidatorComponent
) {

  fun <T> buildOperatorCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?,
    operator: FilterOperator?
  ) {
    when (operator) {
      null -> {}
      FilterOperator.EQUAL -> createEqualCriteria(criteriaBuilder, joinedKeys, value)
      FilterOperator.IN -> createInCriteria(criteriaBuilder, joinedKeys, value)
      FilterOperator.ILIKE -> createIlikeCriteria(criteriaBuilder, joinedKeys, value)
    }
  }

  private fun <T> createEqualCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?
  ) {
    if (value == null) {
      criteriaBuilder.where(joinedKeys).isNull
    } else {
      criteriaBuilder.where(joinedKeys).eq(value)
    }
  }

  private fun <T> createInCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?
  ) {
    operatorValidatorComponent.castToInOperator(value)?.let {
      criteriaBuilder.where(joinedKeys).`in`(it)
    }
  }

  private fun <T> createIlikeCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?
  ) {
    operatorValidatorComponent.castToILikeOperator(value)?.let {
      criteriaBuilder.where(joinedKeys).like(false).value(it).noEscape()
    }
  }
}
