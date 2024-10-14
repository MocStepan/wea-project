package cz.tul.backend.book.dto

import cz.tul.backend.book.entity.BookAuthorLink_
import cz.tul.backend.book.entity.BookAuthor_
import cz.tul.backend.book.entity.BookCategoryLink_
import cz.tul.backend.book.entity.BookCategory_
import cz.tul.backend.book.entity.Book_
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterCriteriaDTO
import cz.tul.backend.common.filter.dto.FilterDTO

data class BookFilterDTO(
  override val size: Int = 20,
  override val page: Int = 0,
  val isbn13: FilterCriteriaDTO? = null,
  val isbn10: FilterCriteriaDTO? = null,
  val title: FilterCriteriaDTO? = null,
  val authors: FilterCriteriaDTO? = null,
  val categories: FilterCriteriaDTO? = null
) : FilterDTO {

  override fun toFilterCriteria(): MutableList<FilterCriteria<Any>> {
    val list = mutableListOf<FilterCriteria<Any>>()

    isbn13?.let {
      list.add(FilterCriteria.convertAndBuild(it, Book_.ISBN13))
    }

    isbn10?.let {
      list.add(FilterCriteria.convertAndBuild(it, Book_.ISBN10))
    }

    title?.let {
      list.add(FilterCriteria.convertAndBuild(it, Book_.TITLE))
    }

    authors?.let {
      val joinKeys = listOf(
        Book_.AUTHORS,
        BookAuthorLink_.AUTHOR
      )
      list.add(FilterCriteria.convertAndBuild(it, BookAuthor_.NAME, joinKeys))
    }

    categories?.let {
      val joinKeys = listOf(
        Book_.CATEGORIES,
        BookCategoryLink_.CATEGORY
      )
      list.add(FilterCriteria.convertAndBuild(it, BookCategory_.NAME, joinKeys))
    }

    return list
  }
}
