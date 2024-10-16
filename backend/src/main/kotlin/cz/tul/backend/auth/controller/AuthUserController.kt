package cz.tul.backend.auth.controller

import cz.tul.backend.auth.base.dto.AuthJwtClaims
import cz.tul.backend.auth.dto.AuthUserDTO
import cz.tul.backend.auth.service.AuthUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthUserController(
  private val authUserService: AuthUserService
) {

  @GetMapping("/v1/auth/user")
  fun getAuthUser(authentication: Authentication): ResponseEntity<AuthUserDTO?> {
    val principal = authentication.principal as AuthJwtClaims
    val response = authUserService.getAuthUser(principal)
    val status = if (response == null) HttpStatus.NOT_FOUND else HttpStatus.OK
    return ResponseEntity(response, status)
  }
}
