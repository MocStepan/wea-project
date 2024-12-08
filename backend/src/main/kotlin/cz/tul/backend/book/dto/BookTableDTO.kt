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
  val price: Double
  val isbn13: String
  val disabled: Boolean

  @get:Mapping("authors.author.name")
  val authors: Set<String>

  @get:Mapping("categories.category.name")
  val categories: Set<String>
}
