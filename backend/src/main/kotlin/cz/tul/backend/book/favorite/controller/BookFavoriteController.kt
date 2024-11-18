package cz.tul.backend.book.favorite.controller

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.book.favorite.service.BookFavoriteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Book favorite", description = "Operations with book favorite")
class BookFavoriteController(
  private val bookFavoriteService: BookFavoriteService
) {

  @Operation(summary = "Add book to favorite", description = "Endpoint to add a book to favorites")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Book was added to favorites successfully"),
    ApiResponse(responseCode = "400", description = "Book was not added to favorites")
  )
  @PostMapping("/v1/book/{id}/favorite")
  fun addBookToFavorite(
    @PathVariable id: Long,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = bookFavoriteService.addBookToFavorite(id, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }

  @Operation(summary = "Delete book from favorite", description = "Endpoint to delete a book from favorites")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Book was deleted from favorites successfully"),
    ApiResponse(responseCode = "400", description = "Book was not deleted from favorites")
  )
  @DeleteMapping("/v1/book/{id}/favorite")
  fun deleteBookFromFavorite(
    @PathVariable id: Long,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = bookFavoriteService.deleteBookFromFavorite(id, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }
}
