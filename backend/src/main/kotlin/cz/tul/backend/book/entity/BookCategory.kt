package cz.tul.backend.book.entity

import cz.tul.backend.personinfo.entity.PersonInfoCategory
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class BookCategory(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  val name: String,
  @OneToMany(mappedBy = "category", orphanRemoval = true)
  val bookCategoryLink: Set<BookCategoryLink> = mutableSetOf(),
  @OneToMany(mappedBy = "bookCategory", orphanRemoval = true)
  val personInfoCategory: Set<PersonInfoCategory> = mutableSetOf()
)
