package cz.tul.backend.cart.controller

import cz.tul.backend.cart.dto.CartCreateDTO
import cz.tul.backend.cart.dto.CartFilterDTO
import cz.tul.backend.cart.dto.CartTableDTO
import cz.tul.backend.cart.service.CartFilterService
import cz.tul.backend.cart.service.CartService
import cz.tul.backend.cart.valueobject.PaymentMethod
import cz.tul.backend.common.filter.dto.PageResponseDTO
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication

class CartControllerTests : FeatureSpec({

  feature("create cart") {
    scenario("success") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(paymentMethod = PaymentMethod.CARD, cartItems = emptyList())
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.cartService.createCart(createDTO, claims) } returns true

      val result = spec.cartController.createCart(createDTO, authentication)

      result.statusCode shouldBe HttpStatus.OK
      result.body shouldBe true
    }

    scenario("failure") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(paymentMethod = PaymentMethod.CARD, cartItems = emptyList())
      val claims = createUserClaims()
      val authentication = mockk<Authentication>()

      every { authentication.principal } returns claims
      every { spec.cartService.createCart(createDTO, claims) } returns false

      val result = spec.cartController.createCart(createDTO, authentication)

      result.statusCode shouldBe HttpStatus.BAD_REQUEST
      result.body shouldBe false
    }
  }

  feature("filter carts") {
    scenario("success") {
      val spec = getSpec()

      val filterDTO = CartFilterDTO()
      val pageResponseDTO = mockk<PageResponseDTO<CartTableDTO>>()

      every { spec.cartFilterService.filterCarts(filterDTO) } returns pageResponseDTO

      val result = spec.cartController.filterCarts(filterDTO)

      result.statusCode shouldBe HttpStatus.OK
      result.body shouldBe pageResponseDTO
    }
  }
})

private class CartControllerSpecWrapper(
  val cartService: CartService,
  val cartFilterService: CartFilterService
) {
  val cartController = CartController(cartService, cartFilterService)
}

private fun getSpec() = CartControllerSpecWrapper(mockk(), mockk())
