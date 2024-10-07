package cz.tul.backend.basic.controller

import cz.tul.backend.basic.dto.WelcomeDTO
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger { }

@RestController
@RequestMapping("/api")
class WelcomeController {
  @ApiResponse(description = "Welcome to the backend")
  @GetMapping("/v1/welcome/welcome-text")
  fun welcome(): ResponseEntity<WelcomeDTO> {
    log.trace { "Welcome to the wea application" }
    log.debug { "Welcome to the wea application" }
    log.info { "Welcome to the wea application" }
    log.warn { "Welcome to the wea application" }
    log.error { "Welcome to the wea application" }
    return ResponseEntity.ok(WelcomeDTO("Welcome to the wea application"))
  }
}
