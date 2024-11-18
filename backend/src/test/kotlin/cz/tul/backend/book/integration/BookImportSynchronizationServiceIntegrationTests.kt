package cz.tul.backend.book.integration

import cz.tul.backend.IntegrationTestApplication
import cz.tul.backend.audit.repository.BookStockAuditRepository
import cz.tul.backend.audit.valueobject.AuditType
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookAuthor
import cz.tul.backend.book.entity.BookAuthorLink
import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.entity.BookCategoryLink
import cz.tul.backend.book.repository.BookAuthorLinkRepository
import cz.tul.backend.book.repository.BookAuthorRepository
import cz.tul.backend.book.repository.BookCategoryLinkRepository
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.book.repository.BookRepository
import cz.tul.backend.book.service.synchronization.BookStockSynchronizationService
import cz.tul.backend.utils.IntegrationTestService
import cz.tul.backend.utils.createBook
import cz.tul.backend.utils.createBookImportDTO
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [IntegrationTestApplication::class])
@ActiveProfiles("test")
class BookImportSynchronizationServiceIntegrationTests(
  private val bookStockSynchronizationService: BookStockSynchronizationService,
  private val bookRepository: BookRepository,
  private val bookCategoryRepository: BookCategoryRepository,
  private val bookAuthorRepository: BookAuthorRepository,
  private val bookAuthorLinkRepository: BookAuthorLinkRepository,
  private val bookCategoryLinkRepository: BookCategoryLinkRepository,
  private val integrationTestService: IntegrationTestService,
  private val bookStockAuditRepository: BookStockAuditRepository
) : FunSpec({

  beforeSpec {
    integrationTestService.cleanDatabase()
  }

  fun createBook(index: Int, disabled: Boolean = false): Book {
    return bookRepository.save(
      createBook(
        isbn13 = "978-3-16-148410-$index",
        isbn10 = "3-16-148410-$index",
        title = "The Republic $index",
        disabled = disabled
      )
    )
  }

  fun checkBook(isbn13: String, bookImportDTO: BookImportDTO) {
    val book = bookRepository.findByIsbn13(isbn13)
    requireNotNull(book)
    book.isbn13 shouldBe bookImportDTO.isbn13
    book.isbn10 shouldBe bookImportDTO.isbn10
    book.title shouldBe bookImportDTO.title
    book.subtitle shouldBe bookImportDTO.subtitle
    book.thumbnail shouldBe bookImportDTO.thumbnail
    book.description shouldBe bookImportDTO.description
    book.publishedYear shouldBe bookImportDTO.publishedYear
    book.numPages shouldBe bookImportDTO.numPages
    book.price shouldBe bookImportDTO.price
    book.disabled shouldBe false

    val categories = bookCategoryRepository.findByBookCategoryLink_Book_Id(book.id)
    categories.size shouldBe 2
    categories.map { it.name }.toSet() shouldBe setOf("Philosophy", "Science")

    val authors = bookAuthorRepository.findByBookAuthorLink_Book_Id(book.id)
    authors.size shouldBe 2
    authors.map { it.name }.toSet() shouldBe setOf("Plato", "Hippocrates")
  }

  fun checkCategories() {
    val categories = bookCategoryRepository.findAll()
    categories.size shouldBe 2
    categories.map { it.name }.toSet() shouldBe setOf("Philosophy", "Science")
  }

  fun checkAuthors() {
    val authors = bookAuthorRepository.findAll()
    authors.size shouldBe 2
    authors.map { it.name }.toSet() shouldBe setOf("Plato", "Hippocrates")
  }

  fun checkDisabledBooks() {
    val books = bookRepository.findAll()
    books.size shouldBe 4
    books.filter { it.disabled }.size shouldBe 2
    books.filter { !it.disabled }.size shouldBe 2
  }

  fun checkBookStockAuditLogs() {
    val logs = bookStockAuditRepository.findAll()
    logs.size shouldBe 3

    logs.filter { it.auditType == AuditType.CBD_SHOW_BOOK }.size shouldBe 1
    logs.filter { it.auditType == AuditType.CBD_HIDE_BOOK }.size shouldBe 1
    logs.filter { it.auditType == AuditType.CBD_NEW_BOOK }.size shouldBe 1
  }

  test("test book stock synchronization") {
    /**
     * Create book for updating existing book
     */
    val book1 = createBook(1, true)

    /**
     * Check if book is set to disabled
     */
    createBook(2, true)
    createBook(3)

    val category = bookCategoryRepository.save(BookCategory(name = "Philosophy"))
    bookCategoryLinkRepository.save(
      BookCategoryLink(book = book1, category = category)
    )

    val author = bookAuthorRepository.save(BookAuthor(name = "Plato"))
    bookAuthorLinkRepository.save(
      BookAuthorLink(book = book1, author = author)
    )

    val bookImportDTO1 = createBookImportDTO(
      isbn13 = "978-3-16-148410-0",
      isbn10 = "3-16-148410-0",
      categories = "Philosophy, Science",
      authors = "Plato; Hippocrates"
    )
    val bookImportDTO2 = createBookImportDTO(
      isbn13 = "978-3-16-148410-1",
      isbn10 = "3-16-148410-1",
      categories = "Philosophy, Science",
      authors = "Plato; Hippocrates"
    )

    runBlocking {
      bookStockSynchronizationService.synchronizeBooks(listOf(bookImportDTO1, bookImportDTO2))

      repeat(10) {
        val book = bookRepository.findByIsbn13(bookImportDTO1.isbn13)
        if (book != null) return@repeat
        delay(500)
      }

      checkBook(bookImportDTO1.isbn13, bookImportDTO1)
      checkBook(book1.isbn13, bookImportDTO2)
      checkCategories()
      checkAuthors()
      checkDisabledBooks()
      checkBookStockAuditLogs()
    }
  }
})
