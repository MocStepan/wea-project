package cz.tul.backend.audit.service

import cz.tul.backend.audit.entity.BookStockAudit
import cz.tul.backend.audit.entity.BookStockAudit_.creator
import cz.tul.backend.audit.repository.BookStockAuditRepository
import cz.tul.backend.audit.valueobject.AuditType
import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.service.AuthUserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for saving audit logs for book stock.
 */
@Service
@Transactional
class BookStockAuditService(
  private val bookStockAuditRepository: BookStockAuditRepository,
  private val authUserService: AuthUserService
) {

  /**
   * Save audit log for given audit type and [AuthJwtClaims].
   *
   * @param auditType audit type
   * @param claims creator of the audit log [AuthJwtClaims]
   * @param description description of the audit log
   */
  fun saveAuditLog(auditType: AuditType, claims: AuthJwtClaims?, description: String? = null) {
    if (claims == null) {
      auditType.saveLog("Unknown", description)
      return
    }

    authUserService.getReferenceIfExists(claims.authUserId).let {
      auditType.saveLog(it?.email?.value ?: "Unknown", description)
    }
  }

  /**
   * Save audit log for given audit type.
   *
   * @param auditType audit type
   * @param creator creator of the audit log
   * @param description description of the audit log
   */
  fun saveAuditLog(auditType: AuditType, creator: String, description: String? = null) {
    auditType.saveLog(creator, description)
  }

  /**
   * Save audit log for given audit type to database.
   *
   * @param auditType audit type
   * @param creator creator of the audit log
   * @param description description of the audit log
   */
  private fun AuditType.saveLog(creator: String, description: String?) {
    bookStockAuditRepository.save(
      BookStockAudit(
        auditType = this,
        creator = creator,
        description = description
      )
    )
  }
}
