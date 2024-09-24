package cz.tul.backend.basic.controller

import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class WelcomeController {

    @ApiResponse(description = "Welcome to the backend")
    @GetMapping("/")
    fun welcome(): String {
        return "Welcome to the backend"
    }
}
