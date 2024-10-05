package cz.tul.backend.auth.service

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class AuthPasswordEncoderTests : FeatureSpec({

  val authPasswordEncoder = AuthPasswordEncoder()

  feature("encode") {
    scenario("should return encoded password") {
      val rawPassword = "password"

      val encodedPassword = authPasswordEncoder.encode(rawPassword)

      authPasswordEncoder.matches(rawPassword, encodedPassword) shouldBe true
    }
  }

  feature("matches") {
    scenario("should return true for matching passwords") {
      val rawPassword = "password"

      val encodedPassword = authPasswordEncoder.encode(rawPassword)

      val matches = authPasswordEncoder.matches(rawPassword, encodedPassword)

      matches shouldBe true
    }

    scenario("should return false for non-matching passwords") {
      val rawPassword = "password"

      val encodedPassword = authPasswordEncoder.encode(rawPassword)

      val matches = authPasswordEncoder.matches("wrongPassword", encodedPassword)

      matches shouldBe false
    }
  }
})
