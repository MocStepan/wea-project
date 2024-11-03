package cz.tul.backend.book.integration

import cz.tul.backend.IntegrationTestApplication
import cz.tul.backend.book.dto.BookFilterDTO
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
import cz.tul.backend.book.service.BookFilterService
import cz.tul.backend.common.filter.dto.FilterCriteriaDTO
import cz.tul.backend.common.filter.valueobject.FilterOperator
import cz.tul.backend.common.filter.valueobject.FilterSort
import cz.tul.backend.utils.IntegrationTestService
import cz.tul.backend.utils.createBook
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [IntegrationTestApplication::class])
@ActiveProfiles("test")
class BookFilterServiceIntegrationTests(
  private val bookFilterService: BookFilterService,
  private val bookRepository: BookRepository,
  private val bookCategoryRepository: BookCategoryRepository,
  private val bookAuthorRepository: BookAuthorRepository,
  private val bookAuthorLinkRepository: BookAuthorLinkRepository,
  private val bookCategoryLinkRepository: BookCategoryLinkRepository,
  private val integrationTestService: IntegrationTestService
) : FunSpec({

  lateinit var book1: Book
  lateinit var book2: Book

  beforeSpec {
    integrationTestService.cleanDatabase()

    book1 = bookRepository.save(
      createBook(
        isbn13 = "978-3-16-148410-0",
        isbn10 = "3-16-148410-0",
        title = "The Republic 0"
      )
    )
    book2 = bookRepository.save(
      createBook(
        isbn13 = "978-3-16-148410-1",
        isbn10 = "3-16-148410-1",
        title = "The Republic 1"
      )
    )
    bookRepository.save(
      createBook(
        isbn13 = "978-3-16-148410-2",
        isbn10 = "3-16-148410-2",
        title = "The Republic 2",
        disabled = true
      )
    )

    val category1 = bookCategoryRepository.save(BookCategory(name = "Philosophy"))
    val category2 = bookCategoryRepository.save(BookCategory(name = "Science"))

    val author1 = bookAuthorRepository.save(BookAuthor(name = "Plato"))

    bookCategoryLinkRepository.saveAll(
      listOf(
        BookCategoryLink(book = book1, category = category1),
        BookCategoryLink(book = book1, category = category2),
        BookCategoryLink(book = book2, category = category2)
      )
    )

    bookAuthorLinkRepository.saveAll(
      listOf(
        BookAuthorLink(book = book1, author = author1),
        BookAuthorLink(book = book2, author = author1)
      )
    )
  }

  test("filter by categories") {
    val filterDTO = BookFilterDTO(
      categories = FilterCriteriaDTO(FilterOperator.EQUAL, "Philosophy")
    )

    val pageResponse = bookFilterService.filterBooks(filterDTO)

    pageResponse.content.size shouldBe 1
    pageResponse.page shouldBe 1
    pageResponse.totalPages shouldBe 1
    pageResponse.size shouldBe 1
    pageResponse.isEmpty shouldBe false

    val first = pageResponse.content.first()
    first.id shouldBe book1.id
    first.categories shouldBe setOf("Philosophy", "Science")
  }

  test("filter books by categories value null") {
    val filterDTO = BookFilterDTO(
      categories = FilterCriteriaDTO(FilterOperator.EQUAL, null)
    )

    val pageResponse = bookFilterService.filterBooks(filterDTO)

    pageResponse.content.size shouldBe 0
    pageResponse.page shouldBe 1
    pageResponse.totalPages shouldBe 0
    pageResponse.size shouldBe 0
    pageResponse.isEmpty shouldBe true
  }

  test("filter books by categories and sorting them descending") {
    val filterDTO = BookFilterDTO(
      categories = FilterCriteriaDTO(sort = FilterSort.DESC)
    )

    val pageResponse = bookFilterService.filterBooks(filterDTO)

    pageResponse.content.size shouldBe 2
    pageResponse.page shouldBe 1
    pageResponse.totalPages shouldBe 1
    pageResponse.size shouldBe 2
    pageResponse.isEmpty shouldBe false

    val first = pageResponse.content.first()
    first.id shouldBe book1.id
    first.categories shouldBe setOf("Science", "Philosophy")
  }

  test("filter books by isbn13") {
    val filterDTO = BookFilterDTO(
      isbn13 = FilterCriteriaDTO(FilterOperator.EQUAL, book1.isbn13)
    )

    val pageResponse = bookFilterService.filterBooks(filterDTO)

    pageResponse.content.size shouldBe 1
    pageResponse.page shouldBe 1
    pageResponse.totalPages shouldBe 1
    pageResponse.size shouldBe 1
    pageResponse.isEmpty shouldBe false

    val first = pageResponse.content.first()
    first.id shouldBe book1.id
  }

  test("filter books by isbn10") {
    val filterDTO = BookFilterDTO(
      isbn10 = FilterCriteriaDTO(FilterOperator.EQUAL, book1.isbn10)
    )

    val pageResponse = bookFilterService.filterBooks(filterDTO)

    pageResponse.content.size shouldBe 1
    pageResponse.page shouldBe 1
    pageResponse.totalPages shouldBe 1
    pageResponse.size shouldBe 1
    pageResponse.isEmpty shouldBe false

    val first = pageResponse.content.first()
    first.id shouldBe book1.id
  }

  test("filter books by title") {
    val filterDTO = BookFilterDTO(
      title = FilterCriteriaDTO(FilterOperator.EQUAL, book1.title)
    )

    val pageResponse = bookFilterService.filterBooks(filterDTO)

    pageResponse.content.size shouldBe 1
    pageResponse.page shouldBe 1
    pageResponse.totalPages shouldBe 1
    pageResponse.size shouldBe 1
    pageResponse.isEmpty shouldBe false

    val first = pageResponse.content.first()
    first.id shouldBe book1.id
  }

  test("filter categories with in operator") {
    val filterDTO = BookFilterDTO(
      categories = FilterCriteriaDTO(FilterOperator.IN, "[Philosophy, Science]")
    )

    val pageResponse = bookFilterService.filterBooks(filterDTO)

    pageResponse.content.size shouldBe 2
    pageResponse.page shouldBe 1
    pageResponse.totalPages shouldBe 1
    pageResponse.size shouldBe 2
    pageResponse.isEmpty shouldBe false

    val first = pageResponse.content.first()
    first.id shouldBe book1.id
    first.categories shouldBe setOf("Philosophy", "Science")
  }

  test("filter books by authors with ilike operator") {
    val filterDTO = BookFilterDTO(
      authors = FilterCriteriaDTO(FilterOperator.ILIKE, "Pla")
    )

    val pageResponse = bookFilterService.filterBooks(filterDTO)

    pageResponse.content.size shouldBe 2
    pageResponse.page shouldBe 1
    pageResponse.totalPages shouldBe 1
    pageResponse.size shouldBe 2
    pageResponse.isEmpty shouldBe false

    val first = pageResponse.content.first()
    first.id shouldBe book1.id
    first.authors shouldBe setOf("Plato")
  }
})
