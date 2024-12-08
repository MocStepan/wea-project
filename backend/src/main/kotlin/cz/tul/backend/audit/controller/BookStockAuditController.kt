package cz.tul.backend.audit.controller

import cz.tul.backend.audit.dto.BookStockAuditFilterDTO
import cz.tul.backend.audit.dto.BookStockAuditTableDTO
import cz.tul.backend.audit.service.BookStockAuditFilterService
import cz.tul.backend.common.filter.dto.PageResponseDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Book stock audit", description = "Operations with book stock audit")
class BookStockAuditController(
  private val bookStockAuditFilterService: BookStockAuditFilterService
) {

  @Operation(summary = "Filter book stock audit", description = "Filter book stock audit by given filter")
  @ApiResponse(responseCode = "200", description = "Book stock audit filtered successfully")
  /*@PreAuthorize("@authComponent.isAdmin(authentication.principal)")*/
  @PostMapping("/v1/audit/book-stock/filter")
  fun filterBookStockAudit(
    @RequestBody filterDTO: BookStockAuditFilterDTO
  ): ResponseEntity<PageResponseDTO<BookStockAuditTableDTO>> {
    val response = bookStockAuditFilterService.filterBookStockAudits(filterDTO)
    return ResponseEntity.ok(response)
  }
}
