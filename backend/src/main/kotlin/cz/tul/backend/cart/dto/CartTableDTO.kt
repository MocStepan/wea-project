package cz.tul.backend.cart.dto

import com.blazebit.persistence.view.EntityView
import com.blazebit.persistence.view.IdMapping
import com.blazebit.persistence.view.Mapping
import cz.tul.backend.cart.entity.Cart
import cz.tul.backend.cart.valueobject.PaymentMethod
import java.time.LocalDateTime

@EntityView(Cart::class)
interface CartTableDTO {

  @get:IdMapping
  val id: Long

  val paymentMethod: PaymentMethod
  val totalPrice: Double
  val createdDateTime: LocalDateTime

  @get:Mapping("cartItems")
  val carItems: List<CarItemTableDTO>
}
