package cz.tul.backend.auth.controller

import cz.tul.backend.auth.dto.AuthLoginDTO
import cz.tul.backend.auth.dto.AuthRegisterDTO
import cz.tul.backend.auth.service.AuthCookieService
import cz.tul.backend.auth.service.AuthPasswordService
import cz.tul.backend.shared.serviceresult.ServiceResult
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
  fun logout(response: HttpServletResponse): ResponseEntity<Any> {
    authCookieService.logout(response)
    return ResponseEntity(HttpStatus.OK)
  }

  @PostMapping("/v1/auth/register")
  fun register(@RequestBody authRegisterDTO: AuthRegisterDTO): ResponseEntity<*> {
    return when (val result = authPasswordService.register(authRegisterDTO)) {
      is ServiceResult.Success -> ResponseEntity(result.data, HttpStatus.OK)
      is ServiceResult.Error -> ResponseEntity(result.error.message, HttpStatus.BAD_REQUEST)
    }
  }

  // TODO: add invoke refresh token endpoint (login with refresh token)

}
