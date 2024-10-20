package cz.tul.backend.book.controller

import cz.tul.backend.book.dto.BookAuthorOptionView
import cz.tul.backend.book.dto.BookCategoryOptionView
import cz.tul.backend.book.dto.BookFilterDTO
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.dto.BookTableDTO
import cz.tul.backend.book.service.BookFilterService
import cz.tul.backend.book.service.BookService
import cz.tul.backend.common.filter.dto.PageResponseDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Book", description = "Endpoints for book management operations")
class BookController(
  private val bookFilterService: BookFilterService,
  private val bookService: BookService
) {

  @Operation(summary = "Filter books", description = "Endpoint to filter books based on various criteria")
  @ApiResponse(responseCode = "200", description = "Books filtered successfully")
  @PostMapping("/v1/book/filter")
  fun filterBooks(
    @RequestBody filterDTO: BookFilterDTO
  ): ResponseEntity<PageResponseDTO<BookTableDTO>> {
    val response = bookFilterService.filterBooks(filterDTO)
    return ResponseEntity(response, HttpStatus.OK)
  }

  @Operation(summary = "Import books", description = "Endpoint to import a list of books")
  @ApiResponse(responseCode = "200", description = "Books imported successfully")
  @PostMapping("/v1/book/import")
  fun importBooks(
    @RequestBody importDTOs: List<BookImportDTO>
  ): ResponseEntity<Any> {
    bookService.saveImportedBooks(importDTOs)
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
}
