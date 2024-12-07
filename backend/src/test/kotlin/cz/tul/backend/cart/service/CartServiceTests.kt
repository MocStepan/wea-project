package cz.tul.backend.cart.service

import cz.tul.backend.cart.dto.CartCreateDTO
import cz.tul.backend.cart.dto.CartItemCreateDTO
import cz.tul.backend.cart.entity.Cart
import cz.tul.backend.cart.repository.CartRepository
import cz.tul.backend.cart.valueobject.CartStatus
import cz.tul.backend.cart.valueobject.PaymentMethod
import cz.tul.backend.personinfo.service.PersonInfoAddressService
import cz.tul.backend.personinfo.service.PersonInfoService
import cz.tul.backend.utils.createPersonInfo
import cz.tul.backend.utils.createPersonInfoAddress
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify

class CartServiceTests : FeatureSpec({

  feature("create cart") {
    scenario("success with cash method") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(PaymentMethod.CASH, listOf(CartItemCreateDTO(1, 1)))
      val claims = createUserClaims()
      val personInfo = createPersonInfo()
      val billingAddress = createPersonInfoAddress()

      val cartSlot = slot<Cart>()

      every { spec.personInfoService.getReferenceIfExistsByAuthUserId(claims.authUserId) } returns personInfo
      every { spec.personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfo.id) } returns billingAddress
      every { spec.cartRepository.save(any()) } answers { firstArg() }
      every { spec.cartItemService.createCartItems(any(), createDTO.cartItems) } returns true
      every { spec.cartItemService.getTotalPriceByCardId(any()) } returns 100.0
      every { spec.cartRepository.save(capture(cartSlot)) } answers { firstArg() }

      val result = spec.cartService.createCart(createDTO, claims)

      result shouldBe true
      val captured = cartSlot.captured
      captured.personInfo shouldBe personInfo
      captured.totalPrice shouldBe 150.0
      captured.paymentMethod shouldBe createDTO.paymentMethod
      captured.status shouldBe CartStatus.PROCESSED

      val deliveryAddress = captured.deliveryAddress
      deliveryAddress.country shouldBe billingAddress.country
      deliveryAddress.city shouldBe billingAddress.city
      deliveryAddress.street shouldBe billingAddress.street
      deliveryAddress.houseNumber shouldBe billingAddress.houseNumber
      deliveryAddress.zipCode shouldBe billingAddress.zipCode
    }

    scenario("success with bank transfer") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(PaymentMethod.BANK_TRANSFER, listOf(CartItemCreateDTO(1, 1)))
      val claims = createUserClaims()
      val personInfo = createPersonInfo()
      val billingAddress = createPersonInfoAddress()

      val cartSlot = slot<Cart>()

      every { spec.personInfoService.getReferenceIfExistsByAuthUserId(claims.authUserId) } returns personInfo
      every { spec.personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfo.id) } returns billingAddress
      every { spec.cartRepository.save(any()) } answers { firstArg() }
      every { spec.cartItemService.createCartItems(any(), createDTO.cartItems) } returns true
      every { spec.cartItemService.getTotalPriceByCardId(any()) } returns 100.0
      every { spec.cartRepository.save(capture(cartSlot)) } answers { firstArg() }

      val result = spec.cartService.createCart(createDTO, claims)

      result shouldBe true
      cartSlot.captured.totalPrice shouldBe 100.0
    }

    scenario("success with card") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(PaymentMethod.CARD, listOf(CartItemCreateDTO(1, 1)))
      val claims = createUserClaims()
      val personInfo = createPersonInfo()
      val billingAddress = createPersonInfoAddress()

      val cartSlot = slot<Cart>()

      every { spec.personInfoService.getReferenceIfExistsByAuthUserId(claims.authUserId) } returns personInfo
      every { spec.personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfo.id) } returns billingAddress
      every { spec.cartRepository.save(any()) } answers { firstArg() }
      every { spec.cartItemService.createCartItems(any(), createDTO.cartItems) } returns true
      every { spec.cartItemService.getTotalPriceByCardId(any()) } returns 100.0
      every { spec.cartRepository.save(capture(cartSlot)) } answers { firstArg() }

      val result = spec.cartService.createCart(createDTO, claims)

      result shouldBe true
      cartSlot.captured.totalPrice shouldBe 110.0
    }

    scenario("person info not found") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(PaymentMethod.CARD, listOf(CartItemCreateDTO(1, 1)))
      val claims = createUserClaims()

      every { spec.personInfoService.getReferenceIfExistsByAuthUserId(claims.authUserId) } returns null

      val result = spec.cartService.createCart(createDTO, claims)

      result shouldBe false

      verify(exactly = 0) { spec.cartRepository.save(any()) }
    }

    scenario("billing address not found") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(PaymentMethod.CARD, listOf(CartItemCreateDTO(1, 1)))
      val claims = createUserClaims()
      val personInfo = createPersonInfo()

      every { spec.personInfoService.getReferenceIfExistsByAuthUserId(claims.authUserId) } returns personInfo
      every { spec.personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfo.id) } returns null

      val result = spec.cartService.createCart(createDTO, claims)

      result shouldBe false

      verify(exactly = 0) { spec.cartRepository.save(any()) }
    }

    scenario("billing address is not valid") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(PaymentMethod.CARD, listOf(CartItemCreateDTO(1, 1)))
      val claims = createUserClaims()
      val personInfo = createPersonInfo()
      val billingAddress = createPersonInfoAddress(country = null)

      every { spec.personInfoService.getReferenceIfExistsByAuthUserId(claims.authUserId) } returns personInfo
      every { spec.personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfo.id) } returns billingAddress

      val result = spec.cartService.createCart(createDTO, claims)

      result shouldBe false

      verify(exactly = 0) { spec.cartRepository.save(any()) }
    }

    scenario("cart items not created") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(PaymentMethod.CARD, listOf(CartItemCreateDTO(1, 1)))
      val claims = createUserClaims()
      val personInfo = createPersonInfo()
      val billingAddress = createPersonInfoAddress()

      every { spec.personInfoService.getReferenceIfExistsByAuthUserId(claims.authUserId) } returns personInfo
      every { spec.personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfo.id) } returns billingAddress
      every { spec.cartRepository.save(any()) } answers { firstArg() }
      every { spec.cartItemService.createCartItems(any(), createDTO.cartItems) } returns false
      every { spec.cartRepository.delete(any()) } just runs

      val result = spec.cartService.createCart(createDTO, claims)

      result shouldBe false
    }

    scenario("cart items are empty") {
      val spec = getSpec()

      val createDTO = CartCreateDTO(PaymentMethod.BANK_TRANSFER, listOf())
      val claims = createUserClaims()
      val personInfo = createPersonInfo()
      val billingAddress = createPersonInfoAddress()

      every { spec.personInfoService.getReferenceIfExistsByAuthUserId(claims.authUserId) } returns personInfo
      every { spec.personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfo.id) } returns billingAddress

      val result = spec.cartService.createCart(createDTO, claims)

      result shouldBe false

      verify(exactly = 0) { spec.cartRepository.save(any()) }
    }
  }
})

private class CartServiceSpecWrapper(
  val cartRepository: CartRepository,
  val cartItemService: CartItemService,
  val personInfoService: PersonInfoService,
  val personInfoAddressService: PersonInfoAddressService
) {
  val cartService = CartService(
    cartRepository,
    cartItemService,
    personInfoService,
    personInfoAddressService
  )
}

private fun getSpec() = CartServiceSpecWrapper(mockk(), mockk(), mockk(), mockk())
