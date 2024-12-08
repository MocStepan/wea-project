package cz.tul.backend.audit.dto

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.audit.entity.BookStockAudit_
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterCriteriaDTO
import cz.tul.backend.common.filter.dto.FilterDTO

/**
 * Data transfer object for filtering [BookStockAudit] entities.
 */
data class BookStockAuditFilterDTO(
  override val size: Int = 20,
  override val page: Int = 0,
  val creator: FilterCriteriaDTO? = null,
  val auditType: FilterCriteriaDTO? = null,
  val createdDateTime: FilterCriteriaDTO? = null
) : FilterDTO {

  /**
   * Converts [FilterCriteriaDTO] of this object to a list of [FilterCriteria].
   *
   * @return list of filter criteria
   */
  override fun toFilterCriteria(objectMapper: ObjectMapper): MutableList<FilterCriteria<Any>> {
    val list = mutableListOf<FilterCriteria<Any>>()

    creator?.let {
      list.add(FilterCriteria.convertAndBuild(it, BookStockAudit_.CREATOR))
    }

    auditType?.let {
      list.add(FilterCriteria.convertAndBuild(it, BookStockAudit_.AUDIT_TYPE))
    }

    createdDateTime?.let {
      list.add(FilterCriteria.convertAndBuild(it, BookStockAudit_.CREATED_DATE_TIME))
    }

    return list
  }
}
