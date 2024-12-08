package cz.tul.backend.book.rating.controller

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.book.rating.dto.BookRatingCreateDTO
import cz.tul.backend.book.rating.dto.BookRatingDTO
import cz.tul.backend.book.rating.service.BookRatingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "BookRating", description = "Endpoints for book rating management operations")
class BookRatingController(
  private val bookRatingService: BookRatingService
) {

  @Operation(summary = "Get book rating", description = "Endpoint to get rating for a book with given id")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Book rating retrieved successfully"),
    ApiResponse(responseCode = "400", description = "Book rating retrieval failed")
  )
  @GetMapping("/v1/book/{id}/rating")
  fun getBookRating(
    @PathVariable id: Long,
    authentication: Authentication
  ): ResponseEntity<BookRatingDTO?> {
    val claims = authentication.principal as AuthJwtClaims
    val response = bookRatingService.getBookRating(id, claims)
    val status = if (response != null) HttpStatus.OK else HttpStatus.NO_CONTENT
    return ResponseEntity(response, status)
  }

  @Operation(summary = "Add book rating", description = "Endpoint to add a rating for a book with given id")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Book rating added successfully"),
    ApiResponse(responseCode = "400", description = "Book rating addition failed")
  )
  @PostMapping("/v1/book/{id}/rating")
  fun createBookRating(
    @PathVariable id: Long,
    @RequestBody createDTO: BookRatingCreateDTO,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = bookRatingService.createBookRating(id, createDTO, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }

  @Operation(
    summary = "Edit book rating",
    description = "Endpoint to edit a rating for a book with given id"
  )
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Book rating edited successfully"),
    ApiResponse(responseCode = "400", description = "Book rating edition failed")
  )
  @PutMapping("/v1/book/{id}/rating")
  fun editBookRating(
    @PathVariable id: Long,
    @RequestBody updateDTO: BookRatingCreateDTO,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = bookRatingService.editBookRating(id, updateDTO, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }

  @Operation(
    summary = "Delete book rating from user",
    description = "Endpoint to delete a rating for a book with given id"
  )
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Book rating deleted successfully"),
    ApiResponse(responseCode = "400", description = "Book rating deletion failed")
  )
  @DeleteMapping("/v1/book/{id}/rating")
  fun deleteBookRating(
    @PathVariable id: Long,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = bookRatingService.deleteBookRating(id, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }
}
