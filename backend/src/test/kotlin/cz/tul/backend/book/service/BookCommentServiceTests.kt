package cz.tul.backend.book.service

import cz.tul.backend.auth.service.AuthUserService
import cz.tul.backend.book.dto.BookCommentCreateDTO
import cz.tul.backend.book.entity.BookComment
import cz.tul.backend.book.repository.BookCommentRepository
import cz.tul.backend.book.repository.BookRepository
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.createBook
import cz.tul.backend.utils.createUserClaims
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class BookCommentServiceTests : FeatureSpec({

  feature("create book comment") {
    scenario("success") {
      val spec = getSpec()

      val createDTO = BookCommentCreateDTO("comment")
      val authUser = createAuthUser()
      val book = createBook()
      val claims = createUserClaims()

      val bookCommentSlot = slot<BookComment>()

      every { spec.authUserService.getReferenceIfExists(claims.authUserId) } returns authUser
      every { spec.bookRepository.findByIdOrNull(0L) } returns book
      every { spec.bookCommentRepository.save(capture(bookCommentSlot)) } answers { firstArg() }

      val result = spec.bookCommentService.createBookComment(0L, createDTO, claims)

      result shouldBe true
      bookCommentSlot.captured.book shouldBe book
      bookCommentSlot.captured.authUser shouldBe authUser
      bookCommentSlot.captured.comment shouldBe createDTO.comment
    }

    scenario("invalid create dto") {
      val spec = getSpec()

      val createDTO = BookCommentCreateDTO("")
      val claims = createUserClaims()

      val result = spec.bookCommentService.createBookComment(0L, createDTO, claims)

      result shouldBe false

      verify(exactly = 0) { spec.bookCommentRepository.save(any()) }
    }

    scenario("auth user not found") {
      val spec = getSpec()

      val createDTO = BookCommentCreateDTO("comment")
      val claims = createUserClaims()

      every { spec.authUserService.getReferenceIfExists(claims.authUserId) } returns null

      val result = spec.bookCommentService.createBookComment(0L, createDTO, claims)

      result shouldBe false

      verify(exactly = 0) { spec.bookCommentRepository.save(any()) }
    }

    scenario("book not found") {
      val spec = getSpec()

      val createDTO = BookCommentCreateDTO("comment")
      val authUser = createAuthUser()
      val claims = createUserClaims()

      every { spec.authUserService.getReferenceIfExists(claims.authUserId) } returns authUser
      every { spec.bookRepository.findByIdOrNull(0L) } returns null

      val result = spec.bookCommentService.createBookComment(0L, createDTO, claims)

      result shouldBe false

      verify(exactly = 0) { spec.bookCommentRepository.save(any()) }
    }
  }
})

private class BookCommentServiceSpecWrapper(
  val bookRepository: BookRepository,
  val bookCommentRepository: BookCommentRepository,
  val authUserService: AuthUserService
) {

  val bookCommentService: BookCommentService = BookCommentService(
    bookCommentRepository,
    bookRepository,
    authUserService
  )
}

private fun getSpec() = BookCommentServiceSpecWrapper(mockk(), mockk(), mockk())
