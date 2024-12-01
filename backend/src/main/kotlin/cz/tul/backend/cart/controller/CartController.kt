package cz.tul.backend.cart.controller

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.cart.dto.CartCreateDTO
import cz.tul.backend.cart.service.CartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Cart", description = "Cart API")
class CartController(
  private val cartService: CartService
) {

  @PostMapping("/v1/cart")
  @Operation(summary = "Create cart", description = "Create cart with given cart items")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Cart was created"),
    ApiResponse(responseCode = "400", description = "Cart was not created")
  )
  fun createCart(
    @RequestBody createDTO: CartCreateDTO,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = cartService.createCart(createDTO, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }
}
