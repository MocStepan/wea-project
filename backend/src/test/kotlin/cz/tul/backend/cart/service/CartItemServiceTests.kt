package cz.tul.backend.cart.service

import cz.tul.backend.book.service.BookService
import cz.tul.backend.cart.dto.CartItemCreateDTO
import cz.tul.backend.cart.entity.CartItem
import cz.tul.backend.cart.repository.CartItemRepository
import cz.tul.backend.utils.createBook
import cz.tul.backend.utils.createCart
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class CartItemServiceTests : FeatureSpec({

  feature("create cart") {
    scenario("success") {
      val spec = getSpec()

      val createDTOs = listOf(CartItemCreateDTO(1, 1))
      val cart = createCart()
      val book = createBook()

      val cartItemSlots = slot<List<CartItem>>()

      every { spec.bookService.getReferenceIfExists(1) } returns book
      every { spec.cartItemRepository.saveAll(capture(cartItemSlots)) } answers { firstArg() }

      val result = spec.cartItemService.createCartItems(cart, createDTOs)

      result shouldBe true
      val captured = cartItemSlots.captured.first()
      captured.cart shouldBe cart
      captured.book shouldBe book
      captured.quantity shouldBe 1
      captured.price shouldBe book.price
    }

    scenario("book not found") {
      val spec = getSpec()

      val createDTOs = listOf(CartItemCreateDTO(1, 1))
      val cart = createCart()

      every { spec.bookService.getReferenceIfExists(1) } returns null

      val result = spec.cartItemService.createCartItems(cart, createDTOs)

      result shouldBe false
    }
  }

  feature("get total price") {
    scenario("success") {
      val spec = getSpec()

      val cartId = 1L
      val cartItems = listOf(
        CartItem(1, createCart(), createBook(), 1, 10.0),
        CartItem(2, createCart(), createBook(), 2, 20.0)
      )

      every { spec.cartItemRepository.findByCart_Id(cartId) } returns cartItems

      val result = spec.cartItemService.getTotalPriceByCardId(cartId)

      result shouldBe 30.0
    }
  }
})

private class CartItemServiceSpecWrapper(
  val cartItemRepository: CartItemRepository,
  val bookService: BookService
) {
  val cartItemService = CartItemService(
    cartItemRepository,
    bookService
  )
}

private fun getSpec() = CartItemServiceSpecWrapper(mockk(), mockk())
