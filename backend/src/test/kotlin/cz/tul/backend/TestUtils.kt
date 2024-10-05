package cz.tul.backend

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import cz.tul.backend.auth.base.valueobject.AuthUserRole
import cz.tul.backend.auth.base.valueobject.EmailAddress
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.entity.RefreshToken
import cz.tul.backend.shared.jackson.TrimmingStringDeserializer
import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.springframework.http.ResponseCookie
import java.time.Duration

val objectMapper: ObjectMapper = jacksonObjectMapper()
  .registerModule(JavaTimeModule())
  .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  .setSerializationInclusion(JsonInclude.Include.NON_NULL)
  .registerModule(SimpleModule().addDeserializer(String::class.java, TrimmingStringDeserializer()))

fun createAuthUser(
  id: Long = 0L,
  firstName: String = "firstName",
  lastName: String = "lastName",
  email: EmailAddress = EmailAddress("example@example.com"),
  password: String = "password",
  role: AuthUserRole = AuthUserRole.USER,
  refreshToken: Set<RefreshToken> = emptySet()
): AuthUser {
  return AuthUser(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    password = password,
    role = role,
    refreshToken = refreshToken
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
