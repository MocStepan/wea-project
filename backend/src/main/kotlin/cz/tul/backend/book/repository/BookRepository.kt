package cz.tul.backend.book.repository

import cz.tul.backend.book.entity.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long>
