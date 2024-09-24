package cz.tul.backend.basic.controller

import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class WelcomeController {

    @ApiResponse(description = "Welcome to the backend")
    @GetMapping("/v1/welcome/welcome-text")
    fun welcome(): ResponseEntity<String> {
        return ResponseEntity.ok("Welcome to the wea application")
    }
}
