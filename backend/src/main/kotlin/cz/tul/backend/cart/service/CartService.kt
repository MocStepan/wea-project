package cz.tul.backend.cart.service

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.cart.dto.CartCreateDTO
import cz.tul.backend.cart.entity.Cart
import cz.tul.backend.cart.entity.CartDeliveryAddress
import cz.tul.backend.cart.repository.CartRepository
import cz.tul.backend.personinfo.entity.PersonInfo
import cz.tul.backend.personinfo.entity.PersonInfoAddress
import cz.tul.backend.personinfo.service.PersonInfoAddressService
import cz.tul.backend.personinfo.service.PersonInfoService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Service for handling carts.
 */
@Service
@Transactional
class CartService(
  private val cartRepository: CartRepository,
  private val cartItemService: CartItemService,
  private val personInfoService: PersonInfoService,
  private val personInfoAddressService: PersonInfoAddressService
) {

  /**
   * Create cart for given createDTOs and claims.
   *
   * @param createDTOs list of cart items to create
   * @param claims claims of the user
   * @return true if cart was created, false otherwise
   * @see CartCreateDTO
   */
  fun createCart(createDTO: CartCreateDTO, claims: AuthJwtClaims): Boolean {
    val personInfo = getPersonInfo(claims.authUserId) ?: return false
    val billingAddress = getBillingAddress(personInfo.id) ?: return false

    if (createDTO.cartItems.isEmpty()) {
      log.warn { "Cart items are empty for user ${claims.authUserId}" }
      return false
    }

    val cart = Cart.from(personInfo, CartDeliveryAddress.from(billingAddress), createDTO.paymentMethod, 0.0)
    val savedCart = cartRepository.save(cart)

    val created = cartItemService.createCartItems(savedCart, createDTO.cartItems)
    if (!created) {
      cartRepository.delete(savedCart)
      log.warn { "Cart items not created for cart ${savedCart.id}" }
      return false
    }

    val totalPrice = cartItemService.getTotalPriceByCardId(savedCart.id)
    savedCart.totalPrice = createDTO.paymentMethod.addPaymentToPrice(totalPrice)
    cartRepository.save(savedCart)
    return true
  }

  /**
   * Get person info for given authUserId.
   *
   * @param authUserId id of the user
   * @return person info or null
   * @see PersonInfo
   */
  private fun getPersonInfo(authUserId: Long): PersonInfo? {
    val personInfo = personInfoService.getReferenceIfExistsByAuthUserId(authUserId)
    if (personInfo == null) {
      log.warn { "PersonInfo not found for user $authUserId" }
      return null
    }
    return personInfo
  }

  /**
   * Get billing address for given personInfoId.
   *
   * @param personInfoId id of the person info
   * @return billing address or null
   * @see PersonInfoAddress
   */
  private fun getBillingAddress(personInfoId: Long): PersonInfoAddress? {
    val billingAddress = personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfoId)
    if (billingAddress == null) {
      log.warn { "Billing address not found for user $personInfoId" }
      return null
    }

    if (!billingAddress.isValidForSubmit()) {
      log.warn { "Billing address: ${billingAddress.id} is not valid for submitting for cart" }
      return null
    }
    return billingAddress
  }
}
