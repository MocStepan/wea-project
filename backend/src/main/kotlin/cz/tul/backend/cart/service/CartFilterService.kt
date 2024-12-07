package cz.tul.backend.cart.service

import cz.tul.backend.cart.dto.CartTableDTO
import cz.tul.backend.cart.entity.Cart
import cz.tul.backend.cart.entity.Cart_
import cz.tul.backend.common.filter.blaze.EntityViewFilterComponent
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterDTO
import cz.tul.backend.common.filter.dto.PageResponseDTO
import cz.tul.backend.common.filter.valueobject.FilterSort
import org.springframework.stereotype.Service

/**
 * Service for filtering carts.
 */
@Service
class CartFilterService(
  private val entityViewFilterComponent: EntityViewFilterComponent
) {

  /**
   * Filters carts with given filter data by using [EntityViewFilterComponent].
   *
   * @param filterDTO filter data
   * @return page response with filtered carts
   */
  fun filterCarts(filterDTO: FilterDTO): PageResponseDTO<CartTableDTO> {
    return entityViewFilterComponent.filterEntityView(
      filterDTO,
      CartTableDTO::class.java,
      Cart::class.java,
      FilterCriteria.buildOrderCriteria(Cart_.ID, FilterSort.ASC)
    )
  }
}
