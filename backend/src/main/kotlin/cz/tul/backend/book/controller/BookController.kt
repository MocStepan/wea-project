package cz.tul.backend.book.controller

import cz.tul.backend.book.dto.BookFilterDTO
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.dto.BookTableDTO
import cz.tul.backend.book.service.BookFilterService
import cz.tul.backend.book.service.BookService
import cz.tul.backend.common.filter.dto.PageResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class BookController(
  private val bookFilterService: BookFilterService,
  private val bookService: BookService
) {

  @PostMapping("/v1/book/filter")
  fun filterBooks(
    @RequestBody filterDTO: BookFilterDTO
  ): ResponseEntity<PageResponseDTO<BookTableDTO>> {
    val response = bookFilterService.filterBooks(filterDTO)
    return ResponseEntity(response, HttpStatus.OK)
  }

  @PostMapping("/v1/book/import")
  fun importBooks(
    @RequestBody importDTOs: List<BookImportDTO>
  ): ResponseEntity<Any> {
    bookService.saveImportedBooks(importDTOs)
    return ResponseEntity.ok().build()
  }

  @GetMapping("/v1/book/categories")
  fun getAllCategories(): ResponseEntity<Set<String>> {
    val categories = bookService.getAllCategories()
    return ResponseEntity(categories, HttpStatus.OK)
  }

  @GetMapping("/v1/book/authors")
  fun getAllAuthors(): ResponseEntity<Set<String>> {
    val authors = bookService.getAllAuthors()
    return ResponseEntity(authors, HttpStatus.OK)
  }
}
