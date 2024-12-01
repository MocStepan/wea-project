package cz.tul.backend.cart.service

import cz.tul.backend.book.service.BookService
import cz.tul.backend.cart.dto.CartCreateDTO
import cz.tul.backend.cart.dto.CartItemCreateDTO
import cz.tul.backend.cart.entity.Cart
import cz.tul.backend.cart.entity.CartItem
import cz.tul.backend.cart.repository.CartItemRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Service for handling cart items.
 */
@Service
@Transactional
class CartItemService(
  private val cartItemRepository: CartItemRepository,
  private val bookService: BookService
) {

  /**
   * Create cart items for given cart and createDTO.
   *
   * @param cart cart for which the cart items will be created
   * @param createDTO list of cart items to create
   * @see CartCreateDTO
   */
  fun createCartItems(cart: Cart, createDTO: List<CartItemCreateDTO>): Boolean {
    val cartItems = mutableListOf<CartItem>()

    createDTO.forEach {
      val book = bookService.getReferenceIfExists(it.bookId)
      if (book == null) {
        log.warn { "Book with id ${it.bookId} does not exist" }
        return false
      }
      cartItems.add(CartItem.from(cart, book, it.quantity))
    }

    cartItemRepository.saveAll(cartItems)
    return true
  }

  /**
   * Get total price of cart items for given cart id.
   *
   * @param cartId id of the cart
   * @return total price of cart items
   */
  fun getTotalPriceByCardId(cartId: Long): Double {
    return cartItemRepository.findByCart_Id(cartId).sumOf { it.price }
  }
}
