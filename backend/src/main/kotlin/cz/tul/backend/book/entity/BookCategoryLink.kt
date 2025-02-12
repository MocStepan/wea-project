package cz.tul.backend.book.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class BookCategoryLink(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @ManyToOne(optional = false)
  val book: Book,
  @ManyToOne(optional = false)
  val category: BookCategory
)
