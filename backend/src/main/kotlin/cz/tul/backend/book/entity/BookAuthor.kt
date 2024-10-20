package cz.tul.backend.book.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class BookAuthor(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  val name: String,
  @OneToMany(mappedBy = "author", orphanRemoval = true)
  val bookAuthorLink: Set<BookAuthorLink> = mutableSetOf()
)
