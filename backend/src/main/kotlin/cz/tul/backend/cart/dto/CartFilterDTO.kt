package cz.tul.backend.cart.dto

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.cart.entity.Cart_
import cz.tul.backend.cart.valueobject.PaymentMethod
import cz.tul.backend.common.addNotNull
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterCriteriaDTO
import cz.tul.backend.common.filter.dto.FilterDTO

data class CartFilterDTO(
  override val size: Int = 20,
  override val page: Int = 0,
  val paymentMethod: FilterCriteriaDTO? = null,
  val totalPrice: FilterCriteriaDTO? = null,
  val createdDateTime: FilterCriteriaDTO? = null
) : FilterDTO {

  /**
   * Converts [FilterCriteriaDTO] of this object to a list of [FilterCriteria].
   *
   * @return list of filter criteria
   */
  override fun toFilterCriteria(objectMapper: ObjectMapper): MutableList<FilterCriteria<Any>> {
    val list = mutableListOf<FilterCriteria<Any>>()

    paymentMethod?.let {
      list.addNotNull(
        FilterCriteria.convertAndBuild(
          it,
          Cart_.PAYMENT_METHOD,
          objectMapper,
          PaymentMethod::class.java
        )
      )
      list.add(FilterCriteria.convertAndBuild(it, Cart_.PAYMENT_METHOD))
    }

    totalPrice?.let {
      list.add(FilterCriteria.convertAndBuild(it, Cart_.TOTAL_PRICE))
    }

    createdDateTime?.let {
      list.add(FilterCriteria.convertAndBuild(it, Cart_.CREATED_DATE_TIME))
    }

    return list
  }
}
