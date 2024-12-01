package cz.tul.backend.cart.valueobject

import kotlin.math.floor

enum class PaymentMethod {
  CASH,
  BANK_TRANSFER,
  CARD;

  fun addPaymentToPrice(price: Double): Double {
    return when (this) {
      CASH -> price + 50.0
      BANK_TRANSFER -> price
      CARD -> floor(price * 1.1)
    }
  }
}
