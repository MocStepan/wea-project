package cz.tul.backend.common.filter.blaze

import com.blazebit.persistence.PaginatedCriteriaBuilder
import cz.tul.backend.common.filter.valueobject.FilterOperator
import org.springframework.stereotype.Component

/**
 * Component for building criteria for Blaze-Persistence operators.
 */
@Component
class BlazeOperatorCriteriaBuilder(
  private val operatorValidatorComponent: BlazeOperatorValidatorComponent
) {

  /**
   * Builds criteria for given [operator] and [value].
   *
   * @param criteriaBuilder Blaze-Persistence criteria builder
   * @param joinedKeys joined keys for criteria
   * @param value value for criteria or null
   * @param operator operator for criteria or null
   */
  fun <T> buildOperatorCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?,
    operator: FilterOperator?
  ) {
    when (operator) {
      null -> {}
      FilterOperator.EQUAL -> createEqualCriteria(criteriaBuilder, joinedKeys, value)
      FilterOperator.NOT_EQUAL -> createNotEqualCriteria(criteriaBuilder, joinedKeys, value)
      FilterOperator.IN -> createInCriteria(criteriaBuilder, joinedKeys, value)
      FilterOperator.ILIKE -> createIlikeCriteria(criteriaBuilder, joinedKeys, value)
      FilterOperator.GREATER_THAN -> createGreaterThanCriteria(criteriaBuilder, joinedKeys, value)
      FilterOperator.LESS_THAN -> createLessThanCriteria(criteriaBuilder, joinedKeys, value)
    }
  }

  /**
   * Creates criteria for equal operator, if value is null, then criteria is created for null.
   *
   * @param criteriaBuilder Blaze-Persistence criteria builder
   * @param joinedKeys joined keys for criteria
   * @param value value for criteria or null
   */
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

  private fun <T> createNotEqualCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?
  ) {
    if (value == null) {
      criteriaBuilder.where(joinedKeys).isNotNull
    } else {
      criteriaBuilder.where(joinedKeys).notEq(value)
    }
  }

  /**
   * Creates criteria for in operator.
   *
   * @param criteriaBuilder Blaze-Persistence criteria builder
   * @param joinedKeys joined keys for criteria
   * @param value value for criteria or null
   */
  private fun <T> createInCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?
  ) {
    operatorValidatorComponent.castToInOperator(value)?.let {
      criteriaBuilder.where(joinedKeys).`in`(it)
    }
  }

  /**
   * Creates criteria for ilike operator.
   *
   * @param criteriaBuilder Blaze-Persistence criteria builder
   * @param joinedKeys joined keys for criteria
   * @param value value for criteria
   */
  private fun <T> createIlikeCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?
  ) {
    operatorValidatorComponent.castToILikeOperator(value)?.let {
      criteriaBuilder.where(joinedKeys).like(false).value(it).noEscape()
    }
  }

  private fun <T> createGreaterThanCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?
  ) {
    if (operatorValidatorComponent.isValidComparable(value)) {
      criteriaBuilder.where(joinedKeys).gt(value)
    }
  }

  private fun <T> createLessThanCriteria(
    criteriaBuilder: PaginatedCriteriaBuilder<T>,
    joinedKeys: String,
    value: Any?
  ) {
    if (operatorValidatorComponent.isValidComparable(value)) {
      criteriaBuilder.where(joinedKeys).lt(value)
    }
  }
}
