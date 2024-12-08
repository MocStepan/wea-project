package cz.tul.backend.common.filter.blaze

import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.PaginatedCriteriaBuilder
import com.blazebit.persistence.view.EntityViewManager
import com.blazebit.persistence.view.EntityViewSetting
import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterDTO
import cz.tul.backend.common.filter.dto.PageResponseDTO
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

private const val BASE_ROOT_ALIAS = "rootAlias"

/**
 * Implementation of [EntityViewFilterComponent] that uses Blaze-Persistence for filtering entity views.
 */
@Component
class BlazeFilterComponent(
  private val entityManager: EntityManager,
  private val cbf: CriteriaBuilderFactory,
  private val entityViewManager: EntityViewManager,
  private val operatorCriteriaBuilder: BlazeOperatorCriteriaBuilder,
  private val sortCriteriaBuilder: BlazeSortCriteriaBuilder,
  private val objectMapper: ObjectMapper
) : EntityViewFilterComponent {

  /**
   * Filters entity view by given [filterDTO] and [orderCriteria].
   *
   * @param filterDTO DTO with filter criteria
   * @param entityViewClass class of entity view
   * @param rootEntity class of root entity
   * @param orderCriteria criteria for ordering
   * @param additionCriteria additional criteria for filtering
   * @return [PageResponseDTO] with filtered entity views
   * @see EntityViewFilterComponent.filterEntityView
   */
  override fun <T> filterEntityView(
    filterDTO: FilterDTO,
    entityViewClass: Class<T>,
    rootEntity: Class<*>,
    orderCriteria: FilterCriteria<Any>,
    additionCriteria: FilterCriteria<Any>?
  ): PageResponseDTO<T> {
    val filterCriteria = filterDTO.toFilterCriteria(objectMapper)
    filterCriteria.add(orderCriteria)
    additionCriteria?.let { filterCriteria.add(it) }

    var criteriaBuilder = entityViewManager.applySetting(
      EntityViewSetting.create(entityViewClass),
      cbf.create(entityManager, rootEntity, BASE_ROOT_ALIAS)
    ).page(filterDTO.getStartIndex(), filterDTO.size)

    criteriaBuilder = applyFilterCriteria(filterCriteria, criteriaBuilder)
    val resultList = criteriaBuilder.resultList

    return PageResponseDTO(
      content = resultList,
      size = resultList.size,
      page = resultList.page,
      totalPages = resultList.totalPages,
      isEmpty = resultList.isEmpty()
    )
  }

  /**
   * Applies filter criteria to [PaginatedCriteriaBuilder].
   *
   * @param filterCriteria list of filter criteria
   * @param criteriaBuilder [PaginatedCriteriaBuilder] to apply filter criteria
   * @return [PaginatedCriteriaBuilder] with applied filter criteria
   */
  private fun <T> applyFilterCriteria(
    filterCriteria: List<FilterCriteria<Any>>,
    criteriaBuilder: PaginatedCriteriaBuilder<T>
  ): PaginatedCriteriaBuilder<T> {
    filterCriteria.forEach {
      val joinedKeys = createJoinedKeys(*it.joinsKeys.toTypedArray(), it.key)
      operatorCriteriaBuilder.buildOperatorCriteria(
        criteriaBuilder,
        joinedKeys,
        it.value,
        it.operator
      )
      sortCriteriaBuilder.buildOperatorCriteria(
        criteriaBuilder,
        joinedKeys,
        it.sort
      )
    }
    return criteriaBuilder
  }

  /**
   * Creates joined keys from given keys.
   *
   * @param keys list of keys
   * @return joined keys
   */
  private fun createJoinedKeys(vararg keys: String): String {
    return keys.joinToString(separator = ".")
  }
}
