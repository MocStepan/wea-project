package cz.tul.backend.common.filter.blaze

import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterDTO
import cz.tul.backend.common.filter.dto.PageResponseDTO

interface EntityViewFilterComponent {
  fun <T> filterEntityView(
    filterDTO: FilterDTO,
    entityViewClass: Class<T>,
    rootEntity: Class<*>,
    orderCriteria: FilterCriteria
  ): PageResponseDTO<T>
}
