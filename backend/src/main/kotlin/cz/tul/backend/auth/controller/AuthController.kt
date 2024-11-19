package cz.tul.backend.auth.controller

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.dto.AuthLoginDTO
import cz.tul.backend.auth.dto.AuthRegisterDTO
import cz.tul.backend.auth.service.AuthCookieService
import cz.tul.backend.auth.service.AuthPasswordService
import cz.tul.backend.common.serviceresult.fold
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Authentication", description = "Endpoints for authentication operations")
class AuthController(
  private val authPasswordService: AuthPasswordService,
  private val authCookieService: AuthCookieService
) {

  @Operation(summary = "User login", description = "Endpoint for logging in a user")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Login successful"),
      ApiResponse(responseCode = "400", description = "Invalid login credentials")
    ]
  )
  @PostMapping("/v1/auth/login")
  fun login(
    @RequestBody authLoginDTO: AuthLoginDTO,
    response: HttpServletResponse
  ): ResponseEntity<Any> {
    val result = authPasswordService.login(authLoginDTO, response)
    val status = if (result) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(status)
  }

  @Operation(summary = "User logout", description = "Endpoint for logging out a user")
  @ApiResponse(responseCode = "200", description = "Logout successful")
  @PostMapping("/v1/auth/logout")
  fun logout(
    request: HttpServletRequest,
    response: HttpServletResponse,
    authentication: Authentication?
  ): ResponseEntity<Any> {
    val principal = authentication?.principal as AuthJwtClaims?
    authPasswordService.logout(request, response, principal)
    return ResponseEntity(HttpStatus.OK)
  }

  @Operation(summary = "User registration", description = "Endpoint for registering a new user")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "User registered successfully",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(example = "true")
          )
        ]
      ),
      ApiResponse(
        responseCode = "400",
        description = "Invalid data",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(example = "Invalid data")
          )
        ]
      )
    ]
  )
  @PostMapping("/v1/auth/registration")
  fun register(
    @RequestBody authRegisterDTO: AuthRegisterDTO
  ): ResponseEntity<*> {
    return authPasswordService.register(authRegisterDTO).fold(
      { ResponseEntity.ok(it) },
      { ResponseEntity.badRequest().build() }
    )
  }

  @Operation(
    summary = "Invoke refresh token",
    description = "Endpoint to refresh access token using refresh token cookie"
  )
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
      ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    ]
  )
  @PostMapping("/v1/auth/invoke-refresh-token")
  fun invokeRefreshToken(
    request: HttpServletRequest,
    response: HttpServletResponse
  ): ResponseEntity<Any> {
    val result = authCookieService.loginWithRefreshCookie(request, response)
    val status = if (result) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(status)
  }
}
