package cz.tul.backend.audit.repository

import cz.tul.backend.audit.entity.BookStockAudit
import org.springframework.data.jpa.repository.JpaRepository

interface BookStockAuditRepository : JpaRepository<BookStockAudit, Long>
