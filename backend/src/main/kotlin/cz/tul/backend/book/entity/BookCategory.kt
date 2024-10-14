package cz.tul.backend.book.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class BookCategory(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,
  val name: String,
  @OneToMany(mappedBy = "category", orphanRemoval = true)
  val bookCategoryLink: Set<BookCategoryLink> = mutableSetOf()
)
