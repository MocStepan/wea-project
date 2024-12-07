package cz.tul.backend.cart.dto

import com.blazebit.persistence.view.EntityView
import com.blazebit.persistence.view.IdMapping
import com.blazebit.persistence.view.Mapping
import cz.tul.backend.cart.entity.CartItem

@EntityView(CartItem::class)
interface CarItemTableDTO {

  @get:IdMapping
  val id: Long

  val quantity: Int
  val price: Double

  @get:Mapping("book.title")
  val bookName: String
}
