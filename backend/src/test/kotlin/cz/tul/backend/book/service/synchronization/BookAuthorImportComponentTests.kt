package cz.tul.backend.book.service.synchronization

import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookAuthorLink
import cz.tul.backend.book.repository.BookAuthorLinkRepository
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.utils.createBook
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class BookAuthorImportComponentTests : FeatureSpec({

  feature("process book authors") {
    scenario("saves new author to database") {
      val spec = getSpec()

      val book = createBook()
      val authors = "author1"
      val authorSlot = slot<BookAuthor>()
      val authorLinkSlot = slot<BookAuthorLink>()

      every { spec.bookAuthorLinkRepository.existsByBook_IdAndAuthor_Name(0L, any()) } returns false
      every { spec.bookAuthorRepository.findByName(any()) } returns null
      every { spec.bookAuthorRepository.save(capture(authorSlot)) } answers { firstArg() }
      every { spec.bookAuthorLinkRepository.save(capture(authorLinkSlot)) } answers { firstArg() }

      spec.bookAuthorImportComponent.processBookAuthors(authors, book)

      authorSlot.captured.name shouldBe "author1"
      authorLinkSlot.captured.book shouldBe book
      authorLinkSlot.captured.author shouldBe authorSlot.captured
    }

    scenario("book author already exists") {
      val spec = getSpec()

      val book = createBook()
      val authors = "author1"
      val bookAuthor = BookAuthor(name = "author1")
      val authorLinkSlot = slot<BookAuthorLink>()

      every { spec.bookAuthorLinkRepository.existsByBook_IdAndAuthor_Name(0L, any()) } returns false
      every { spec.bookAuthorRepository.findByName("author1") } returns bookAuthor
      every { spec.bookAuthorLinkRepository.save(capture(authorLinkSlot)) } answers { firstArg() }

      spec.bookAuthorImportComponent.processBookAuthors(authors, book)

      authorLinkSlot.captured.book shouldBe book
      authorLinkSlot.captured.author shouldBe bookAuthor
    }

    scenario("book author already linked to book") {
      val spec = getSpec()

      val book = createBook()
      val authors = "author1"

      every { spec.bookAuthorLinkRepository.existsByBook_IdAndAuthor_Name(0L, any()) } returns true

      spec.bookAuthorImportComponent.processBookAuthors(authors, book)

      verify(exactly = 0) { spec.bookAuthorRepository.findByName(any()) }
    }
  }
})

private class BookAuthorImportComponentSpecWrapper(
  val bookAuthorRepository: BookAuthorRepository,
  val bookAuthorLinkRepository: BookAuthorLinkRepository
) {
  val bookAuthorImportComponent = BookAuthorImportComponent(bookAuthorRepository, bookAuthorLinkRepository)
}

private fun getSpec() = BookAuthorImportComponentSpecWrapper(
  mockk(),
  mockk()
)
