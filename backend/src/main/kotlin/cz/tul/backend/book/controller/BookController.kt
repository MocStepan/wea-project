package cz.tul.backend.book.controller

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.book.dto.BookAuthorOptionView
import cz.tul.backend.book.dto.BookCategoryOptionView
import cz.tul.backend.book.dto.BookCommentCreateDTO
import cz.tul.backend.book.dto.BookDetailDTO
import cz.tul.backend.book.dto.BookFilterDTO
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.dto.BookTableDTO
import cz.tul.backend.book.service.BookCommentService
import cz.tul.backend.book.service.BookFilterService
import cz.tul.backend.book.service.BookService
import cz.tul.backend.book.service.synchronization.BookStockSynchronizationService
import cz.tul.backend.common.filter.dto.PageResponseDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.websocket.server.PathParam
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Book", description = "Endpoints for book management operations")
class BookController(
  private val bookFilterService: BookFilterService,
  private val bookService: BookService,
  private val bookCommentService: BookCommentService,
  private val bookStockSynchronizationService: BookStockSynchronizationService
) {

  @Operation(summary = "Filter books", description = "Endpoint to filter books based on various criteria")
  @ApiResponse(responseCode = "200", description = "Books filtered successfully")
  @PostMapping("/v1/book/filter")
  fun filterBooks(
    @RequestBody filterDTO: BookFilterDTO,
    @PathParam("favorite") favorite: Boolean,
    authentication: Authentication?
  ): ResponseEntity<PageResponseDTO<BookTableDTO>> {
    val claims = authentication?.principal as AuthJwtClaims?
    val response = bookFilterService.filterBooks(filterDTO, favorite, claims)
    return ResponseEntity(response, HttpStatus.OK)
  }

  @Operation(summary = "Import books", description = "Endpoint to import a list of books")
  @ApiResponse(responseCode = "200", description = "Books imported successfully")
  @PostMapping("/v1/book/import")
  fun importBooks(
    @RequestBody importDTOs: List<BookImportDTO>
  ): ResponseEntity<Any> {
    bookStockSynchronizationService.synchronizeBooks(importDTOs)
    return ResponseEntity.ok().build()
  }

  @Operation(summary = "Get category option views", description = "Endpoint to get a set of all book categories")
  @ApiResponse(
    responseCode = "200",
    description = "Categories retrieved successfully"

  )
  @GetMapping("/v1/book/categories")
  fun getCategoryOptionViews(): ResponseEntity<Set<BookCategoryOptionView>> {
    val categories = bookService.getAllCategories()
    return ResponseEntity(categories, HttpStatus.OK)
  }

  @Operation(summary = "Get author option views", description = "Endpoint to get a set of all book authors")
  @ApiResponse(
    responseCode = "200",
    description = "Authors retrieved successfully"
  )
  @GetMapping("/v1/book/authors")
  fun getAuthorOptionViews(): ResponseEntity<Set<BookAuthorOptionView>> {
    val authors = bookService.getAllAuthors()
    return ResponseEntity(authors, HttpStatus.OK)
  }

  @Operation(summary = "Get book detail", description = "Endpoint to get detail of a book with given id")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Book detail retrieved successfully"),
    ApiResponse(responseCode = "404", description = "Book with given id not found")
  )
  @GetMapping("/v1/book/{id}")
  fun getBookDetail(
    @PathVariable id: Long,
    authentication: Authentication?
  ): ResponseEntity<BookDetailDTO?> {
    val claims = authentication?.principal as AuthJwtClaims?
    val bookDetail = bookService.getBookDetail(id, claims)
    val status = if (bookDetail == null) HttpStatus.NOT_FOUND else HttpStatus.OK
    return ResponseEntity(bookDetail, status)
  }

  @Operation(summary = "Create book comment", description = "Endpoint to create a comment for a book with given id")
  @ApiResponses(
    ApiResponse(responseCode = "200", description = "Book comment created successfully"),
    ApiResponse(responseCode = "400", description = "Book comment creation failed")
  )
  @PostMapping("/v1/book/{id}/comment")
  fun createBookComment(
    @PathVariable id: Long,
    @RequestBody createDTO: BookCommentCreateDTO,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = bookCommentService.createBookComment(id, createDTO, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }
}
