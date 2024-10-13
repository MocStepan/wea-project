package cz.tul.backend.book.integration

import cz.tul.backend.IntegrationTestApplication
import cz.tul.backend.book.dto.BookFilterDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookCategory
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
class BookFilterServiceTests(
  private val bookFilterService: BookFilterService,
  private val bookRepository: BookRepository,
  private val bookCategoryRepository: BookCategoryRepository,
  private val integrationTestService: IntegrationTestService
) : FunSpec({

  lateinit var book1: Book
  lateinit var book2: Book

  beforeSpec {
    integrationTestService.cleanDatabase()

    book1 = bookRepository.save(createBook())
    book2 = bookRepository.save(createBook())

    bookCategoryRepository.save(BookCategory(book = book1, name = "Philosophy"))
    bookCategoryRepository.save(BookCategory(book = book2, name = "Science"))
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
    first.categories shouldBe setOf("Philosophy")
  }

  test("filter by categories value null") {
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

  test("filter categories by sorting them descending") {
    val filterDTO = BookFilterDTO(
      categories = FilterCriteriaDTO(
        sort = FilterSort.DESC
      )
    )

    val pageResponse = bookFilterService.filterBooks(filterDTO)

    pageResponse.content.size shouldBe 2
    pageResponse.page shouldBe 1
    pageResponse.totalPages shouldBe 1
    pageResponse.size shouldBe 2
    pageResponse.isEmpty shouldBe false

    val first = pageResponse.content.first()
    first.id shouldBe book2.id
    first.categories shouldBe setOf("Science")
  }
})
