package cz.tul.backend.book.service.synchronization

import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookAuthorLink
import cz.tul.backend.book.repository.BookAuthorLinkRepository
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.utils.splitAuthors
import org.springframework.stereotype.Component

/**
 * Component for importing book authors.
 */
@Component
class BookAuthorImportComponent(
  private val bookAuthorRepository: BookAuthorRepository,
  private val bookAuthorLinkRepository: BookAuthorLinkRepository
) {

  /**
   * Save book authors to database. If author does not exist, create new author and link it to book.
   *
   * @param authors string with authors separated by semicolon
   * @param book book to save authors for
   */
  fun processBookAuthors(authors: String?, book: Book) {
    authors?.splitAuthors()?.forEach { authorName ->
      if (!bookAuthorLinkRepository.existsByBook_IdAndAuthor_Name(book.id, authorName)) {
        val author = bookAuthorRepository.findByName(authorName) ?: bookAuthorRepository.save(
          BookAuthor(name = authorName)
        )
        bookAuthorLinkRepository.save(BookAuthorLink(book = book, author = author))
      }
    }
  }
}
