package cz.tul.backend.common.filter.blaze

import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterDTO
import cz.tul.backend.common.filter.dto.PageResponseDTO

/**
 * Component for filtering entity views based on the provided filter criteria.
 */
interface EntityViewFilterComponent {

  /**
   * Filters entity view based on the provided [filterDTO] and [orderCriteria].
   *
   * @param filterDTO DTO containing filter criteria
   * @param entityViewClass class of the entity view
   * @param rootEntity class of the root entity
   * @param orderCriteria criteria for ordering the results
   * @param additionCriteria additional criteria for filtering
   * @return page response containing filtered entity views
   */
  fun <T> filterEntityView(
    filterDTO: FilterDTO,
    entityViewClass: Class<T>,
    rootEntity: Class<*>,
    orderCriteria: FilterCriteria<Any>,
    additionCriteria: FilterCriteria<Any>? = null
  ): PageResponseDTO<T>
}
