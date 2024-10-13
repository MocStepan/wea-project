package cz.tul.backend.book.service

import cz.tul.backend.book.dto.BookTableDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.Book_
import cz.tul.backend.common.filter.blaze.EntityViewFilterComponent
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterDTO
import cz.tul.backend.common.filter.dto.PageResponseDTO
import cz.tul.backend.common.filter.valueobject.FilterSort
import org.springframework.stereotype.Service

@Service
class BookFilterService(
  private val entityViewFilterComponent: EntityViewFilterComponent
) {

  fun filterBooks(filterDTO: FilterDTO): PageResponseDTO<BookTableDTO> {
    return entityViewFilterComponent.filterEntityView(
      filterDTO,
      BookTableDTO::class.java,
      Book::class.java,
      FilterCriteria.buildOrderCriteria(Book_.ID, FilterSort.ASC)
    )
  }
}
