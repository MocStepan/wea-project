package cz.tul.backend.cart.entity

import cz.tul.backend.cart.valueobject.CartStatus
import cz.tul.backend.cart.valueobject.PaymentMethod
import cz.tul.backend.personinfo.entity.PersonInfo
import cz.tul.backend.personinfo.entity.PersonInfoAddress
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity
class Cart(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @ManyToOne(optional = false)
  val personInfo: PersonInfo,
  @OneToMany(mappedBy = "cart", orphanRemoval = true)
  val cartItems: List<CartItem> = mutableListOf(),
  @Embedded
  val deliveryAddress: CartDeliveryAddress,
  @Enumerated(EnumType.STRING)
  val paymentMethod: PaymentMethod,
  var totalPrice: Double,
  @Enumerated(EnumType.STRING)
  val status: CartStatus = CartStatus.PROCESSED,
  val createdDateTime: LocalDateTime = LocalDateTime.now()
) {
  companion object {
    fun from(
      personInfo: PersonInfo,
      deliveryAddress: CartDeliveryAddress,
      paymentMethod: PaymentMethod,
      totalPrice: Double
    ): Cart {
      return Cart(
        personInfo = personInfo,
        deliveryAddress = deliveryAddress,
        paymentMethod = paymentMethod,
        totalPrice = totalPrice
      )
    }
  }
}

@Embeddable
class CartDeliveryAddress(
  val country: String,
  val city: String,
  val street: String,
  val houseNumber: String,
  val zipCode: String
) {

  companion object {
    fun from(personInfoAddress: PersonInfoAddress): CartDeliveryAddress {
      return CartDeliveryAddress(
        country = personInfoAddress.country!!,
        city = personInfoAddress.city!!,
        street = personInfoAddress.street!!,
        houseNumber = personInfoAddress.houseNumber!!,
        zipCode = personInfoAddress.zipCode!!
      )
    }
  }
}
