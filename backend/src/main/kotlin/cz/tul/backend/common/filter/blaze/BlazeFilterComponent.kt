package cz.tul.backend.common.filter.blaze

import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.PaginatedCriteriaBuilder
import com.blazebit.persistence.view.EntityViewManager
import com.blazebit.persistence.view.EntityViewSetting
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterDTO
import cz.tul.backend.common.filter.dto.PageResponseDTO
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

private const val BASE_ROOT_ALIAS = "rootAlias"

@Component
class BlazeFilterComponent(
  private val entityManager: EntityManager,
  private val cbf: CriteriaBuilderFactory,
  private val entityViewManager: EntityViewManager,
  private val operatorCriteriaBuilder: BlazeOperatorCriteriaBuilder,
  private val sortCriteriaBuilder: BlazeSortCriteriaBuilder
) : EntityViewFilterComponent {
  override fun <T> filterEntityView(
    filterDTO: FilterDTO,
    entityViewClass: Class<T>,
    rootEntity: Class<*>,
    orderCriteria: FilterCriteria
  ): PageResponseDTO<T> {
    val filterCriteria = filterDTO.toFilterCriteria()
    filterCriteria.add(orderCriteria)

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

  private fun <T> applyFilterCriteria(
    filterCriteria: List<FilterCriteria>,
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

  private fun createJoinedKeys(vararg key: String): String {
    return key.joinToString(separator = ".")
  }
}
