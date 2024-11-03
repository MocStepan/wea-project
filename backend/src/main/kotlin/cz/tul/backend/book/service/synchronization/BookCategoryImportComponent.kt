package cz.tul.backend.book.service.synchronization

import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.entity.BookCategoryLink
import cz.tul.backend.book.repository.BookCategoryLinkRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.utils.splitCategories
import org.springframework.stereotype.Component

/**
 * Component for importing book categories.
 */
@Component
class BookCategoryImportComponent(
  private val bookCategoryRepository: BookCategoryRepository,
  private val bookCategoryLinkRepository: BookCategoryLinkRepository
) {

  /**
   * Save book categories to database. If category does not exist, create new category and link it to book.
   *
   * @param categories string with categories separated by comma
   * @param book book to save categories for
   */
  fun processBookCategories(categories: String?, book: Book) {
    categories?.splitCategories()?.forEach { categoryName ->
      if (!bookCategoryLinkRepository.existsByBook_IdAndCategory_Name(book.id, categoryName)) {
        val category = bookCategoryRepository.findByName(categoryName) ?: bookCategoryRepository.save(
          BookCategory(name = categoryName)
        )
        bookCategoryLinkRepository.save(BookCategoryLink(book = book, category = category))
      }
    }
  }
}
