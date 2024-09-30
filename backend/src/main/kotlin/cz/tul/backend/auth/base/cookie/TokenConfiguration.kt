package cz.tul.backend.auth.base.cookie

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.auth.base.cookie.access.AccessTokenClaims
import cz.tul.backend.auth.base.cookie.access.AccessTokenService
import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenClaims
import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenService
import cz.tul.backend.auth.base.jwt.BaseJwtClaimsService
import cz.tul.backend.auth.base.jwt.BaseJwtCookieService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.milliseconds

private const val REFRESH_COOKIE_NAME = "refresh_token"

private const val ACCESS_COOKIE_NAME = "access_token"

@Configuration
class TokenConfiguration {
  @Bean
  fun getRefreshTokenService(
    objectMapper: ObjectMapper,
    @Value("\${spring.auth.jwt.refresh.secret}") secret: String,
    @Value("\${spring.auth.jwt.refresh.duration}") duration: Long,
    @Value("\${spring.auth.jwt.refresh.sameSite}") sameSite: String,
    @Value("\${spring.auth.jwt.refresh.secure}") secure: Boolean
  ): RefreshTokenService {
    val claimsService = BaseJwtClaimsService(objectMapper, RefreshTokenClaims::class.java, secret)
    val cookieService =
      BaseJwtCookieService(REFRESH_COOKIE_NAME, duration.milliseconds, sameSite, secure, claimsService)
    return RefreshTokenService(
      claimsService,
      cookieService
    )
  }

  @Bean
  fun getAccessTokenService(
    objectMapper: ObjectMapper,
    @Value("\${spring.auth.jwt.access.secret}") secret: String,
    @Value("\${spring.auth.jwt.access.duration}") duration: Long,
    @Value("\${spring.auth.jwt.access.sameSite}") sameSite: String,
    @Value("\${spring.auth.jwt.access.secure}") secure: Boolean
  ): AccessTokenService {
    val claimsService = BaseJwtClaimsService(objectMapper, AccessTokenClaims::class.java, secret)
    val cookieService = BaseJwtCookieService(ACCESS_COOKIE_NAME, duration.milliseconds, sameSite, secure, claimsService)
    return AccessTokenService(
      claimsService,
      cookieService
    )
  }
}
