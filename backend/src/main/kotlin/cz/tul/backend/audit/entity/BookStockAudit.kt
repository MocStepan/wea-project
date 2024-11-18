package cz.tul.backend.audit.entity

import cz.tul.backend.audit.valueobject.AuditType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class BookStockAudit(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @Enumerated(EnumType.STRING)
  val auditType: AuditType,
  val creator: String,
  val description: String? = null,
  val createdDateTime: LocalDateTime = LocalDateTime.now()
)
