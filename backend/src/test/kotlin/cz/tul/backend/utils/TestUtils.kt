package cz.tul.backend.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import cz.tul.backend.auth.base.cookie.access.AccessTokenClaims
import cz.tul.backend.auth.base.dto.AuthJwtClaims
import cz.tul.backend.auth.base.valueobject.AuthRole
import cz.tul.backend.auth.base.valueobject.EmailAddress
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.entity.RefreshToken
import cz.tul.backend.auth.valueobject.Hashed
import cz.tul.backend.book.dto.BookImportDTO
import cz.tul.backend.book.entity.Book
import cz.tul.backend.book.entity.BookComment
import cz.tul.backend.common.filter.dto.PageResponseDTO
import cz.tul.backend.common.jackson.TrimmingStringDeserializer
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.ResponseCookie
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration
import java.util.function.Consumer

val objectMapper: ObjectMapper = jacksonObjectMapper()
  .registerModule(JavaTimeModule())
  .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  .setSerializationInclusion(JsonInclude.Include.NON_NULL)
  .registerModule(SimpleModule().addDeserializer(String::class.java, TrimmingStringDeserializer()))

fun processTransaction(transactionTemplate: TransactionTemplate) {
  every { transactionTemplate.executeWithoutResult(any()) } answers {
    (firstArg() as Consumer<TransactionStatus>).accept(mockk())
  }
}

fun createAuthUser(
  id: Long = 0L,
  firstName: String = "firstName",
  lastName: String = "lastName",
  email: EmailAddress = EmailAddress("example@example.com"),
  password: Hashed = Hashed("password"),
  role: AuthRole = AuthRole.USER,
  refreshToken: Set<RefreshToken> = emptySet()
): AuthUser {
  return AuthUser(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    password = password,
    role = role,
    refreshTokens = refreshToken
  )
}

fun createResponseCookie(
  cookieName: String = "name",
  value: String = "value",
  path: String? = "/",
  sameSite: String? = "Lax",
  maxAxe: Duration = Duration.ZERO,
  secure: Boolean = false,
  httpOnly: Boolean = false
): ResponseCookie {
  return ResponseCookie.from(cookieName, value)
    .httpOnly(httpOnly)
    .path(path)
    .secure(secure)
    .sameSite(sameSite)
    .maxAge(maxAxe)
    .build()
}

fun <T> createPageResponseDTO(
  content: List<T> = emptyList(),
  page: Int = 0,
  size: Int = 0,
  totalPages: Int = 0,
  isEmpty: Boolean = true
): PageResponseDTO<T> {
  return PageResponseDTO(
    content = content,
    page = page,
    size = size,
    totalPages = totalPages,
    isEmpty = isEmpty
  )
}

fun createBook(
  id: Long = 0L,
  isbn13: String = "isbn13",
  isbn10: String = "isbn10",
  title: String = "title",
  subtitle: String? = null,
  thumbnail: String? = null,
  description: String? = null,
  publishedYear: Int? = null,
  averageRating: Double? = null,
  numPages: Int? = null,
  ratingsCount: Int? = null,
  disabled: Boolean = false
): Book {
  return Book(
    id = id,
    title = title,
    isbn13 = isbn13,
    isbn10 = isbn10,
    subtitle = subtitle,
    thumbnail = thumbnail,
    description = description,
    publishedYear = publishedYear,
    averageRating = averageRating,
    numPages = numPages,
    ratingsCount = ratingsCount,
    disabled = disabled
  )
}

fun createBookImportDTO(
  isbn13: String = "978-1-56619-909-4",
  isbn10: String = "156619909X",
  title: String = "The Art of War",
  categories: String? = "Philosophy",
  subtitle: String? = "The Ancient Classic",
  authors: String? = "Sun Tzu",
  thumbnail: String? = "http://books.google.com/books/content?id=1-56619-909-4&printsec=frontcover&img=1&zoom",
  description: String? = "The Art of War is an ancient Chinese military treatise dating from the Late Spring.",
  publishedYear: Int? = -500,
  averageRating: Double? = 4.5,
  numPages: Int? = 48,
  ratingsCount: Int? = 3
): BookImportDTO {
  return BookImportDTO(
    isbn13 = isbn13,
    isbn10 = isbn10,
    title = title,
    categories = categories,
    subtitle = subtitle,
    authors = authors,
    thumbnail = thumbnail,
    description = description,
    publishedYear = publishedYear,
    averageRating = averageRating,
    numPages = numPages,
    ratingsCount = ratingsCount
  )
}

fun createUserClaims(
  id: Long = 0L,
  role: AuthRole = AuthRole.USER,
  email: String = "user@user.com"
): AuthJwtClaims {
  return AccessTokenClaims(
    authUserId = id,
    authRole = role,
    email = email
  )
}

fun createUserClaims(
  authUser: AuthUser
): AuthJwtClaims {
  return AccessTokenClaims(
    authUserId = authUser.id,
    authRole = authUser.role,
    email = authUser.email.value
  )
}

fun createBookComment(
  id: Long = 0L,
  comment: String = "Great book!",
  book: Book = mockk(),
  authUser: AuthUser = mockk()
): BookComment {
  return BookComment(
    id = id,
    comment = comment,
    book = book,
    authUser = authUser
  )
}
