package cz.tul.backend.auth.controller

import cz.tul.backend.auth.dto.AuthLoginDTO
import cz.tul.backend.auth.dto.AuthRegisterDTO
import cz.tul.backend.auth.service.AuthCookieService
import cz.tul.backend.auth.service.AuthPasswordService
import cz.tul.backend.auth.valueobject.AuthPasswordServiceRegisterError
import cz.tul.backend.shared.serviceresult.fold
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthController(
  private val authPasswordService: AuthPasswordService,
  private val authCookieService: AuthCookieService
) {
  @PostMapping("/v1/auth/login")
  fun login(
    @RequestBody authLoginDTO: AuthLoginDTO,
    response: HttpServletResponse
  ): ResponseEntity<Any> {
    val result = authPasswordService.login(authLoginDTO, response)
    val status = if (result) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(status)
  }

  @PostMapping("/v1/auth/logout")
  fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
    authCookieService.clearCookies(request, response)
    return ResponseEntity(HttpStatus.OK)
  }

  @Operation(summary = "Endpoint for registering new user")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "User registered successfully"),
      ApiResponse(responseCode = "400", description = "Invalid data"),
      ApiResponse(responseCode = "409", description = "User already exists")
    ]
  )
  @PostMapping("/v1/auth/register")
  fun register(
    @RequestBody authRegisterDTO: AuthRegisterDTO
  ): ResponseEntity<*> {
    return authPasswordService.register(authRegisterDTO).fold(
      { ResponseEntity.ok(it) },
      {
        when (it) {
          AuthPasswordServiceRegisterError.INVALID_DATA -> ResponseEntity(it.message, HttpStatus.BAD_REQUEST)
          AuthPasswordServiceRegisterError.USER_ALREADY_EXISTS -> ResponseEntity(it.message, HttpStatus.CONFLICT)
        }
      }
    )
  }

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
