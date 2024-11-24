package cz.tul.backend.auth.base.cookie

import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenClaims
import cz.tul.backend.auth.entity.RefreshToken
import cz.tul.backend.utils.createAuthUser
import cz.tul.backend.utils.objectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDateTime

class RefreshTokenJwtServiceTests : FeatureSpec({

  val refreshTokenJwtService = TokenConfiguration().getRefreshTokenJwtService(
    objectMapper,
    "743777217A25432A462D4A404E635266556A586E3272357538782F413F442847",
    60 * 60 * 1000,
    "Lax",
    true
  )

  feature("jwt claims and cookie service") {
    scenario("create token and extract claims") {
      val claims = RefreshTokenClaims(
        refreshTokenId = 1L,
        createdDateTime = LocalDateTime.now()
      )

      val token = refreshTokenJwtService.createCookie(claims)

      val header = token.toString()
      header shouldContain "refresh_token"
      header shouldContain "Path=/"
      header shouldContain "Secure"
      header shouldContain "HttpOnly"
      header shouldContain "SameSite=Lax"
      header shouldContain "Max-Age=3600"

      val cookieValue = header.split("refresh_token=")[1].split(";")[0]
      val extractedClaims = refreshTokenJwtService.extractClaims(cookieValue)

      extractedClaims?.refreshTokenId shouldBe 1L
    }

    scenario("extract claims failed on invalid token") {
      val extractedClaims = refreshTokenJwtService.extractClaims("invalid token")

      extractedClaims shouldBe null
    }

    scenario("clear cookie") {
      val emptyCookie = refreshTokenJwtService.createEmptyCookie()

      val header = emptyCookie.toString()
      header shouldContain "refresh_token="
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
      val refreshToken = RefreshToken(
        authUser = authUser
      )

      val token = refreshTokenJwtService.createClaims(refreshToken)

      token.refreshTokenId shouldBe refreshToken.id
    }
  }
})
