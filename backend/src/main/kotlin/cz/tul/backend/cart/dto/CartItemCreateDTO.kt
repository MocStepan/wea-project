package cz.tul.backend.cart.dto

data class CartItemCreateDTO(
  val bookId: Long,
  val quantity: Int
)
