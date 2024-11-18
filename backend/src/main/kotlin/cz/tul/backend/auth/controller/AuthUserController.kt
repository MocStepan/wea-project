package cz.tul.backend.auth.controller

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.dto.AuthUserDTO
import cz.tul.backend.auth.service.AuthUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Auth User Controller", description = "Operations for the authenticated user.")
class AuthUserController(
  private val authUserService: AuthUserService
) {

  @Operation(summary = "Get the authenticated user", description = "Returns the authenticated user.")
  @ApiResponses(
    ApiResponse(
      responseCode = "200",
      description = "The authenticated user was found.",
      content = [
        Content(
          mediaType = "application/json",
          schema = Schema(implementation = AuthUserDTO::class)
        )
      ]
    ),
    ApiResponse(
      responseCode = "404",
      description = "The authenticated user was not found.",
      content = [
        Content(
          mediaType = "application/json",
          schema = Schema(example = "null")
        )
      ]
    )
  )
  @GetMapping("/v1/auth/user")
  fun getAuthUser(authentication: Authentication): ResponseEntity<AuthUserDTO?> {
    val principal = authentication.principal as AuthJwtClaims
    val response = authUserService.getAuthUser(principal)
    val status = if (response == null) HttpStatus.NOT_FOUND else HttpStatus.OK
    return ResponseEntity(response, status)
  }
}
