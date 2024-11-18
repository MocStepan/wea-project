package cz.tul.backend.audit.service

import cz.tul.backend.audit.entity.BookStockAudit
import cz.tul.backend.audit.repository.BookStockAuditRepository
import cz.tul.backend.audit.valueobject.AuditType
import cz.tul.backend.auth.service.AuthUserService
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class BookStockAuditServiceTests : FeatureSpec({

  feature("save audit log for claims") {
    scenario("success, but user found") {
      val spec = getSpec()

      val authUser = createAuthUser()
      val claims = createUserClaims(authUser)

      val auditLogSlot = slot<BookStockAudit>()

      every { spec.authUserService.getReferenceIfExists(0L) } returns authUser
      every { spec.bookStockAuditRepository.save(capture(auditLogSlot)) } answers { firstArg() }

      spec.service.saveAuditLog(AuditType.CBD_NEW_BOOK, claims)

      val captured = auditLogSlot.captured
      captured.auditType shouldBe AuditType.CBD_NEW_BOOK
      captured.creator shouldBe authUser.email.value
    }

    scenario("success, but user not found") {
      val spec = getSpec()

      val claims = createUserClaims()

      val auditLogSlot = slot<BookStockAudit>()

      every { spec.authUserService.getReferenceIfExists(0L) } returns null
      every { spec.bookStockAuditRepository.save(capture(auditLogSlot)) } answers { firstArg() }

      spec.service.saveAuditLog(AuditType.CBD_NEW_BOOK, claims)

      val captured = auditLogSlot.captured
      captured.auditType shouldBe AuditType.CBD_NEW_BOOK
      captured.creator shouldBe "Unknown"
    }
  }

  feature("save audit log for creator") {
    scenario("success") {
      val spec = getSpec()

      val auditLogSlot = slot<BookStockAudit>()

      every { spec.bookStockAuditRepository.save(capture(auditLogSlot)) } answers { firstArg() }

      spec.service.saveAuditLog(AuditType.CBD_NEW_BOOK, "System")

      val captured = auditLogSlot.captured
      captured.auditType shouldBe AuditType.CBD_NEW_BOOK
      captured.creator shouldBe "System"
    }
  }
})

private class BookStockAuditServiceSpecWrapper(
  val bookStockAuditRepository: BookStockAuditRepository,
  val authUserService: AuthUserService
) {
  val service = BookStockAuditService(bookStockAuditRepository, authUserService)
}

private fun getSpec() = BookStockAuditServiceSpecWrapper(mockk(), mockk())
