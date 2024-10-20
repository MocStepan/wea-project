package cz.tul.backend.auth.base.cookie

import cz.tul.backend.auth.base.cookie.access.AccessTokenClaims
import cz.tul.backend.auth.base.valueobject.AuthRole
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.objectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class AccessTokenServiceJwtTests : FeatureSpec({

  val accessTokenJwtService = TokenConfiguration().getAccessTokenJwtService(
    objectMapper,
    "743777217A25432A462D4A404E635266556A586E3272357538782F413F442847",
    60 * 60 * 1000,
    "Lax",
    true
  )

  feature("jwt claims and cookie service") {
    scenario("create token and extract claims") {
      val claims = AccessTokenClaims(
        authUserId = 1L,
        authRole = AuthRole.USER,
        email = "john.doe@example.com"
      )

      val token = accessTokenJwtService.createCookie(claims)

      val header = token.toString()
      header shouldContain "access_token"
      header shouldContain "Path=/"
      header shouldContain "Secure"
      header shouldContain "HttpOnly"
      header shouldContain "SameSite=Lax"
      header shouldContain "Max-Age=3600"

      val cookieValue = header.split("access_token=")[1].split(";")[0]
      val extractedClaims = accessTokenJwtService.extractClaims(cookieValue)

      extractedClaims?.authUserId shouldBe 1L
      extractedClaims?.authRole shouldBe AuthRole.USER
      extractedClaims?.email shouldBe "john.doe@example.com"
    }

    scenario("extract claims failed on invalid token") {
      val extractedClaims = accessTokenJwtService.extractClaims("invalid token")

      extractedClaims shouldBe null
    }

    scenario("clear cookie") {
      val emptyCookie = accessTokenJwtService.createEmptyCookie()

      val header = emptyCookie.toString()
      header shouldContain "access_token="
      header shouldContain "Path=/"
      header shouldContain "Secure"
      header shouldContain "HttpOnly"
      header shouldContain "SameSite=Lax"
      header shouldContain "Max-Age=0"
    }
  }

  feature("create claims") {
    scenario("success") {
      val authUser = createAuthUser()

      val token = accessTokenJwtService.createClaims(authUser)

      token.authUserId shouldBe authUser.id
      token.authRole shouldBe authUser.role
      token.email shouldBe authUser.email.value
    }
  }
})
