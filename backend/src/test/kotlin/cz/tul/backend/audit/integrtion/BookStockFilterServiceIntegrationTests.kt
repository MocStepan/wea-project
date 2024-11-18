package cz.tul.backend.audit.integrtion

import cz.tul.backend.IntegrationTestApplication
import cz.tul.backend.audit.dto.BookStockAuditFilterDTO
import cz.tul.backend.audit.entity.BookStockAudit
import cz.tul.backend.audit.repository.BookStockAuditRepository
import cz.tul.backend.audit.service.BookStockAuditFilterService
import cz.tul.backend.audit.valueobject.AuditType
import cz.tul.backend.common.filter.dto.FilterCriteriaDTO
import cz.tul.backend.common.filter.valueobject.FilterOperator
import cz.tul.backend.utils.IntegrationTestService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest(classes = [IntegrationTestApplication::class])
@ActiveProfiles("test")
class BookStockFilterServiceIntegrationTests(
  private val bookStockAuditFilterService: BookStockAuditFilterService,
  private val bookStockAuditRepository: BookStockAuditRepository,
  private val integrationTestService: IntegrationTestService
) : FunSpec({

  beforeSpec {
    integrationTestService.cleanDatabase()

    bookStockAuditRepository.save(
      BookStockAudit(
        auditType = AuditType.SIGN_IN,
        creator = "User",
        createdDateTime = LocalDateTime.of(2022, 1, 1, 0, 0)
      )
    )

    bookStockAuditRepository.save(
      BookStockAudit(
        auditType = AuditType.SIGN_OUT,
        creator = "System",
        createdDateTime = LocalDateTime.of(2021, 1, 1, 0, 0)
      )
    )
  }

  context("filter creator") {
    test("filter creator by not equal system") {
      val filterDTO = BookStockAuditFilterDTO(
        creator = FilterCriteriaDTO(FilterOperator.NOT_EQUAL, "System")
      )

      val result = bookStockAuditFilterService.filterBookStockAudits(filterDTO)

      result.size shouldBe 1
      result.content[0].creator shouldBe "User"
    }

    test("filter creator by not equal null") {
      val filterDTO = BookStockAuditFilterDTO(
        creator = FilterCriteriaDTO(FilterOperator.NOT_EQUAL, null)
      )

      val result = bookStockAuditFilterService.filterBookStockAudits(filterDTO)

      result.size shouldBe 2
      result.content[0].creator shouldBe "User"
      result.content[1].creator shouldBe "System"
    }
  }

  context("filter created date time") {
    test("filter by greater than with valid date") {
      val filterDTO = BookStockAuditFilterDTO(
        createdDateTime = FilterCriteriaDTO(FilterOperator.GREATER_THAN, LocalDateTime.of(2021, 1, 1, 0, 0))
      )

      val result = bookStockAuditFilterService.filterBookStockAudits(filterDTO)

      result.size shouldBe 1
      result.content[0].creator shouldBe "User"
      result.content[0].auditType shouldBe AuditType.SIGN_IN
      result.content[0].createdDateTime shouldBe LocalDateTime.of(2022, 1, 1, 0, 0)
      result.content[0].description shouldBe null
    }

    test("filter by greater than with invalid date") {
      val filterDTO = BookStockAuditFilterDTO(
        createdDateTime = FilterCriteriaDTO(FilterOperator.GREATER_THAN, null)
      )

      val result = bookStockAuditFilterService.filterBookStockAudits(filterDTO)

      result.size shouldBe 2
      result.content[0].creator shouldBe "User"
      result.content[1].creator shouldBe "System"
    }

    test("filter by less than with valid date") {
      val filterDTO = BookStockAuditFilterDTO(
        createdDateTime = FilterCriteriaDTO(FilterOperator.LESS_THAN, LocalDateTime.of(2022, 1, 1, 0, 0))
      )

      val result = bookStockAuditFilterService.filterBookStockAudits(filterDTO)

      result.size shouldBe 1
      result.content[0].creator shouldBe "System"
    }

    test("filter by less equal with invalid date") {
      val filterDTO = BookStockAuditFilterDTO(
        createdDateTime = FilterCriteriaDTO(FilterOperator.LESS_THAN, "wrong date")
      )

      val result = bookStockAuditFilterService.filterBookStockAudits(filterDTO)

      result.size shouldBe 2
      result.content[0].creator shouldBe "User"
      result.content[1].creator shouldBe "System"
    }
  }
})
