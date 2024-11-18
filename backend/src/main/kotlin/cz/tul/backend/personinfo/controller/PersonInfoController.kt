package cz.tul.backend.personinfo.controller

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.personinfo.dto.PersonInfoDTO
import cz.tul.backend.personinfo.service.PersonInfoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "PersonInfo", description = "The PersonInfo API")
class PersonInfoController(
  private val personInfoService: PersonInfoService
) {

  @Operation(summary = "Create person info", description = "Create person info from PersonInfoDTO")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Person info created"),
    ApiResponse(responseCode = "400", description = "Person info not created")
  )
  @PostMapping("/v1/person-info")
  fun createPersonInfo(
    @RequestBody personInfoDTO: PersonInfoDTO,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val principal = authentication.principal as AuthJwtClaims
    val response = personInfoService.createPersonInfo(personInfoDTO, principal)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }

  @Operation(summary = "Get person info", description = "Get person info for authenticated user")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Person info found"),
    ApiResponse(responseCode = "404", description = "Person info not found")
  )
  @GetMapping("/v1/person-info")
  fun getPersonInfo(
    authentication: Authentication
  ): ResponseEntity<PersonInfoDTO?> {
    val principal = authentication.principal as AuthJwtClaims
    val response = personInfoService.getPersonInfo(principal)
    val status = if (response != null) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(response, status)
  }

  @Operation(summary = "Update person info", description = "Update person info from PersonInfoDTO")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Person info updated"),
    ApiResponse(responseCode = "400", description = "Person info not updated")
  )
  @PutMapping("/v1/person-info")
  fun updatePersonInfo(
    @RequestBody personInfoDTO: PersonInfoDTO,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val principal = authentication.principal as AuthJwtClaims
    val response = personInfoService.updatePersonInfo(personInfoDTO, principal)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }
}
