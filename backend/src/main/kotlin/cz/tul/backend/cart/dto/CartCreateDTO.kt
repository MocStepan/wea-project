package cz.tul.backend.cart.dto

import cz.tul.backend.cart.valueobject.PaymentMethod

data class CartCreateDTO(
  val paymentMethod: PaymentMethod,
  val cartItems: List<CartItemCreateDTO>
)
