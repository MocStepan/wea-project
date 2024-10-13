package cz.tul.backend.book.dto

import cz.tul.backend.book.entity.BookCategory_
import cz.tul.backend.book.entity.Book_
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterCriteriaDTO
import cz.tul.backend.common.filter.dto.FilterDTO

data class BookFilterDTO(
  override val size: Int = 20,
  override val page: Int = 0,
  val categories: FilterCriteriaDTO? = null
) : FilterDTO {

  override fun toFilterCriteria(): MutableList<FilterCriteria> {
    val list = mutableListOf<FilterCriteria>()

    categories?.let {
      list.add(FilterCriteria.build(it, BookCategory_.NAME, listOf(Book_.CATEGORIES)))
    }

    return list
  }
}
