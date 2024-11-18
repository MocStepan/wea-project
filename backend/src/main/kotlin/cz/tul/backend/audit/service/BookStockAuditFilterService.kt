package cz.tul.backend.audit.service

import cz.tul.backend.audit.dto.BookStockAuditTableDTO
import cz.tul.backend.audit.entity.BookStockAudit
import cz.tul.backend.audit.entity.BookStockAudit_
import cz.tul.backend.common.filter.blaze.EntityViewFilterComponent
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterDTO
import cz.tul.backend.common.filter.dto.PageResponseDTO
import cz.tul.backend.common.filter.valueobject.FilterSort
import org.springframework.stereotype.Service

/**
 * Service for filtering book stock audits.
 */
@Service
class BookStockAuditFilterService(
  private val entityViewFilterComponent: EntityViewFilterComponent
) {

  /**
   * Filters book stock audits with given filter data by using [EntityViewFilterComponent].
   *
   * @param filterDTO filter data
   * @return page response with filtered book stock audits
   */
  fun filterBookStockAudits(filterDTO: FilterDTO): PageResponseDTO<BookStockAuditTableDTO> {
    return entityViewFilterComponent.filterEntityView(
      filterDTO,
      BookStockAuditTableDTO::class.java,
      BookStockAudit::class.java,
      FilterCriteria.buildOrderCriteria(BookStockAudit_.ID, FilterSort.ASC)
    )
  }
}
