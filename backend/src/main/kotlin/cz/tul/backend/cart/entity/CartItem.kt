package cz.tul.backend.cart.entity

import cz.tul.backend.book.entity.Book
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class CartItem(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @ManyToOne(optional = false)
  val cart: Cart,
  @ManyToOne(optional = false)
  val book: Book,
  val quantity: Int,
  val price: Double
) {
  companion object {
    fun from(cart: Cart, book: Book, quantity: Int): CartItem {
      return CartItem(
        cart = cart,
        book = book,
        quantity = quantity,
        price = quantity * book.price
      )
    }
  }
}
