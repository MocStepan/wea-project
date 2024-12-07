package cz.tul.backend.cart.integration

import cz.tul.backend.IntegrationTestApplication
import cz.tul.backend.auth.repository.AuthUserRepository
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.repository.BookRepository
import cz.tul.backend.cart.dto.CartFilterDTO
import cz.tul.backend.cart.repository.CartItemRepository
import cz.tul.backend.cart.repository.CartRepository
import cz.tul.backend.cart.service.CartFilterService
import cz.tul.backend.cart.valueobject.PaymentMethod
import cz.tul.backend.common.filter.dto.FilterCriteriaDTO
import cz.tul.backend.common.filter.valueobject.FilterOperator
import cz.tul.backend.personinfo.repository.PersonInfoRepository
import cz.tul.backend.utils.IntegrationTestService
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createBook
import cz.tul.backend.utils.createCart
import cz.tul.backend.utils.createCartDeliveryAddress
import cz.tul.backend.utils.createCartItem
import cz.tul.backend.utils.createPersonInfo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest(classes = [IntegrationTestApplication::class])
@ActiveProfiles("test")
class CartFilterServiceIntegrationTests(
  private val cartFilterService: CartFilterService,
  private val cartRepository: CartRepository,
  private val cartItemRepository: CartItemRepository,
  private val authUserRepository: AuthUserRepository,
  private val personInfoRepository: PersonInfoRepository,
  private val bookRepository: BookRepository,
  private val integrationTestService: IntegrationTestService
) : FunSpec({

  lateinit var book1: Book
  lateinit var book2: Book

  beforeSpec {
    integrationTestService.cleanDatabase()

    book1 = bookRepository.save(
      createBook(
        isbn13 = "978-3-16-148410-0"
      )
    )
    book2 = bookRepository.save(
      createBook(
        isbn13 = "978-3-16-148410-1"
      )
    )

    val authUser = authUserRepository.save(createAuthUser())
    val personInfo = personInfoRepository.save(createPersonInfo(authUser = authUser))

    val deliveryAddress = createCartDeliveryAddress()
    val cart1 = cartRepository.save(
      createCart(
        personInfo = personInfo,
        deliveryAddress = deliveryAddress,
        createdDateTime = LocalDateTime.of(2021, 1, 1, 0, 0),
        paymentMethod = PaymentMethod.CARD,
        totalPrice = 100.0
      )
    )
    val cart2 = cartRepository.save(
      createCart(
        personInfo = personInfo,
        deliveryAddress = deliveryAddress,
        createdDateTime = LocalDateTime.of(2022, 1, 1, 0, 0),
        paymentMethod = PaymentMethod.CASH,
        totalPrice = 200.0
      )
    )

    cartItemRepository.save(createCartItem(cart = cart1, book = book1))
    cartItemRepository.save(createCartItem(cart = cart2, book = book2))
  }

  test("filter carts by payment method") {
    val filterDTO = CartFilterDTO(
      paymentMethod = FilterCriteriaDTO(
        value = PaymentMethod.CARD,
        operator = FilterOperator.EQUAL
      )
    )

    val response = cartFilterService.filterCarts(filterDTO)

    response.content.size shouldBe 1
    val cart = response.content[0]
    cart.paymentMethod shouldBe PaymentMethod.CARD
    cart.createdDateTime shouldBe cart.createdDateTime
    cart.totalPrice shouldBe 100.0
    cart.carItems.size shouldBe 1
    cart.carItems[0].bookName shouldBe book1.title
  }

  test("filter carts by total price") {
    val filterDTO = CartFilterDTO(
      totalPrice = FilterCriteriaDTO(
        value = 200.0,
        operator = FilterOperator.EQUAL
      )
    )

    val response = cartFilterService.filterCarts(filterDTO)

    response.content.size shouldBe 1
    val cart = response.content[0]
    cart.paymentMethod shouldBe PaymentMethod.CASH
    cart.createdDateTime shouldBe cart.createdDateTime
    cart.totalPrice shouldBe 200.0
    cart.carItems.size shouldBe 1
    cart.carItems[0].bookName shouldBe book2.title
  }

  test("filter carts by created date time") {
    val filterDTO = CartFilterDTO(
      createdDateTime = FilterCriteriaDTO(
        value = LocalDateTime.of(2021, 1, 1, 0, 0),
        operator = FilterOperator.EQUAL
      )
    )

    val response = cartFilterService.filterCarts(filterDTO)

    response.content.size shouldBe 1
    val cart = response.content[0]
    cart.paymentMethod shouldBe PaymentMethod.CARD
    cart.createdDateTime shouldBe cart.createdDateTime
    cart.totalPrice shouldBe 100.0
    cart.carItems.size shouldBe 1
    cart.carItems[0].bookName shouldBe book1.title
  }
})
