package cz.tul.backend.book.service.synchronization

import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.entity.BookCategoryLink
import cz.tul.backend.book.repository.BookCategoryLinkRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.utils.createBook
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class BookCategoryImportComponentTests : FeatureSpec({

  feature("process book category") {
    scenario("saves new category to database") {
      val spec = getSpec()

      val book = createBook()
      val categories = "category1"
      val categorySlot = slot<BookCategory>()
      val categoryLinkSlot = slot<BookCategoryLink>()

      every { spec.bookCategoryLinkRepository.existsByBook_IdAndCategory_Name(0L, categories) } returns false
      every { spec.bookCategoryRepository.findByName(any()) } returns null
      every { spec.bookCategoryRepository.save(capture(categorySlot)) } answers { firstArg() }
      every { spec.bookCategoryLinkRepository.save(capture(categoryLinkSlot)) } answers { firstArg() }

      spec.bookCategoryImportComponent.processBookCategories(categories, book)

      categorySlot.captured.name shouldBe "category1"
      categoryLinkSlot.captured.book shouldBe book
      categoryLinkSlot.captured.category shouldBe categorySlot.captured
    }

    scenario("book category already exists") {
      val spec = getSpec()

      val book = createBook()
      val categories = "category1"
      val bookCategory = BookCategory(name = "category1")
      val categoryLinkSlot = slot<BookCategoryLink>()

      every { spec.bookCategoryLinkRepository.existsByBook_IdAndCategory_Name(0L, categories) } returns false
      every { spec.bookCategoryRepository.findByName("category1") } returns bookCategory
      every { spec.bookCategoryLinkRepository.save(capture(categoryLinkSlot)) } answers { firstArg() }

      spec.bookCategoryImportComponent.processBookCategories(categories, book)

      categoryLinkSlot.captured.book shouldBe book
      categoryLinkSlot.captured.category shouldBe bookCategory
    }

    scenario("book category already linked to book") {
      val spec = getSpec()

      val book = createBook()
      val categories = "category1"

      every { spec.bookCategoryLinkRepository.existsByBook_IdAndCategory_Name(0L, categories) } returns true

      spec.bookCategoryImportComponent.processBookCategories(categories, book)

      verify(exactly = 0) { spec.bookCategoryRepository.findByName(any()) }
    }
  }
})

private class BookCategoryImportComponentSpecWrapper(
  val bookCategoryRepository: BookCategoryRepository,
  val bookCategoryLinkRepository: BookCategoryLinkRepository
) {
  val bookCategoryImportComponent = BookCategoryImportComponent(bookCategoryRepository, bookCategoryLinkRepository)
}

private fun getSpec() = BookCategoryImportComponentSpecWrapper(
  mockk(),
  mockk()
)
