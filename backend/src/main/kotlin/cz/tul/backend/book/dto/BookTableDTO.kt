package cz.tul.backend.book.dto

import com.blazebit.persistence.view.EntityView
import com.blazebit.persistence.view.IdMapping
import com.blazebit.persistence.view.Mapping
import cz.tul.backend.book.entity.Book

@EntityView(Book::class)
interface BookTableDTO {

  @get:IdMapping
  val id: Long

  val title: String
  val subtitle: String?
  val thumbnail: String?
  val description: String?

  @get:Mapping("authors.name")
  val authors: Set<String>

  @get:Mapping("categories.name")
  val categories: Set<String>
}
