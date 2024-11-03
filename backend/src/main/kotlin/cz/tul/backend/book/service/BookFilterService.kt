package cz.tul.backend.book.service

import cz.tul.backend.book.dto.BookTableDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.Book_
import cz.tul.backend.common.filter.blaze.EntityViewFilterComponent
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterCriteriaDTO
import cz.tul.backend.common.filter.dto.FilterDTO
import cz.tul.backend.common.filter.dto.PageResponseDTO
import cz.tul.backend.common.filter.valueobject.FilterOperator
import cz.tul.backend.common.filter.valueobject.FilterSort
import org.springframework.stereotype.Service

/**
 * Service for filtering books.
 */
@Service
class BookFilterService(
  private val entityViewFilterComponent: EntityViewFilterComponent
) {

  /**
   * Filters books with given filter data by using [EntityViewFilterComponent].
   *
   * @param filterDTO filter data
   * @return page response with filtered books
   */
  fun filterBooks(filterDTO: FilterDTO): PageResponseDTO<BookTableDTO> {
    return entityViewFilterComponent.filterEntityView(
      filterDTO,
      BookTableDTO::class.java,
      Book::class.java,
      FilterCriteria.buildOrderCriteria(Book_.ID, FilterSort.ASC),
      FilterCriteria.convertAndBuild(FilterCriteriaDTO(FilterOperator.EQUAL, false), Book_.DISABLED)
    )
  }
}
