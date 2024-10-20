package cz.tul.backend.auth.base.cookie

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.auth.base.cookie.access.AccessTokenClaims
import cz.tul.backend.auth.base.cookie.access.AccessTokenJwtService
import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenClaims
import cz.tul.backend.auth.base.cookie.refresh.RefreshTokenJwtService
import cz.tul.backend.auth.base.jwt.BaseJwtClaimsService
import cz.tul.backend.auth.base.jwt.BaseJwtCookieService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.milliseconds

private const val REFRESH_COOKIE_NAME = "refresh_token"

private const val ACCESS_COOKIE_NAME = "access_token"

/**
 * Configuration class for [RefreshTokenJwtService] and [AccessTokenJwtService] beans.
 */
@Configuration
class TokenConfiguration {

  /**
   * Bean for creating a [RefreshTokenJwtService] instance with [BaseJwtClaimsService] and [BaseJwtCookieService].
   * Uses values from application.yml for the secret, duration, sameSite, and secure parameters.
   *
   */
  @Bean
  fun getRefreshTokenJwtService(
    objectMapper: ObjectMapper,
    @Value("\${auth.jwt.refresh.secret}") secret: String,
    @Value("\${auth.jwt.refresh.duration}") duration: Long,
    @Value("\${auth.jwt.refresh.sameSite}") sameSite: String,
    @Value("\${auth.jwt.refresh.secure}") secure: Boolean
  ): RefreshTokenJwtService {
    val claimsService = BaseJwtClaimsService(objectMapper, RefreshTokenClaims::class.java, secret)
    val cookieService =
      BaseJwtCookieService(REFRESH_COOKIE_NAME, duration.milliseconds, sameSite, secure, claimsService)
    return RefreshTokenJwtService(
      claimsService,
      cookieService
    )
  }

  /**
   * Bean for creating an [AccessTokenJwtService] instance with [BaseJwtClaimsService] and [BaseJwtCookieService].
   * Uses values from application.yml for the secret, duration, sameSite, and secure parameters.
   */
  @Bean
  fun getAccessTokenJwtService(
    objectMapper: ObjectMapper,
    @Value("\${auth.jwt.access.secret}") secret: String,
    @Value("\${auth.jwt.access.duration}") duration: Long,
    @Value("\${auth.jwt.access.sameSite}") sameSite: String,
    @Value("\${auth.jwt.access.secure}") secure: Boolean
  ): AccessTokenJwtService {
    val claimsService = BaseJwtClaimsService(objectMapper, AccessTokenClaims::class.java, secret)
    val cookieService = BaseJwtCookieService(ACCESS_COOKIE_NAME, duration.milliseconds, sameSite, secure, claimsService)
    return AccessTokenJwtService(
      claimsService,
      cookieService
    )
  }
}
