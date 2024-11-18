package cz.tul.backend.audit.dto

import com.blazebit.persistence.view.EntityView
import com.blazebit.persistence.view.IdMapping
import cz.tul.backend.audit.entity.BookStockAudit
import cz.tul.backend.audit.valueobject.AuditType
import java.time.LocalDateTime

@EntityView(BookStockAudit::class)
interface BookStockAuditTableDTO {

  @get:IdMapping
  val id: Long
  val auditType: AuditType
  val creator: String
  val description: String?
  val createdDateTime: LocalDateTime
}
