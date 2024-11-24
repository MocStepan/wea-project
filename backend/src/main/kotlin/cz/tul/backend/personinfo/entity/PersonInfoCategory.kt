package cz.tul.backend.personinfo.entity

import cz.tul.backend.book.entity.BookCategory
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class PersonInfoCategory(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @ManyToOne(optional = false)
  val personInfo: PersonInfo,
  @ManyToOne(optional = false)
  val bookCategory: BookCategory
)
