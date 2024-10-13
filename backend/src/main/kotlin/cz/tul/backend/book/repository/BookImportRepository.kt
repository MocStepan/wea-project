package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.BookImport
import org.springframework.data.jpa.repository.JpaRepository

interface BookImportRepository : JpaRepository<BookImport, Long>
