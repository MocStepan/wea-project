package cz.tul.backend.audit.controller

import cz.tul.backend.audit.dto.BookStockAuditFilterDTO
import cz.tul.backend.audit.dto.BookStockAuditTableDTO
import cz.tul.backend.audit.service.BookStockAuditFilterService
import cz.tul.backend.common.filter.dto.PageResponseDTO
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus

class BookStockAuditControllerTests : FeatureSpec({

  feature("filter book stock audit") {
    scenario("success") {
      val spec = getSpec()

      val filterDTO = BookStockAuditFilterDTO()
      val pageResponse = mockk<PageResponseDTO<BookStockAuditTableDTO>>()

      every { spec.bookStockAuditFilterService.filterBookStockAudits(filterDTO) } returns pageResponse

      val response = spec.controller.filterBookStockAudit(filterDTO)

      response.body shouldBe pageResponse
      response.statusCode shouldBe HttpStatus.OK
    }
  }
})

private class BookStockAuditControllerSpecWrapper(
  val bookStockAuditFilterService: BookStockAuditFilterService
) {
  val controller = BookStockAuditController(bookStockAuditFilterService)
}

private fun getSpec() = BookStockAuditControllerSpecWrapper(mockk())
