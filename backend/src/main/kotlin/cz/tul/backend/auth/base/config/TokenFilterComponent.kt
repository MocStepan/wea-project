package cz.tul.backend.auth.base.config

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.base.cookie.access.AccessTokenJwtService
import cz.tul.backend.auth.base.cookie.utils.getCookieValue
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

/**
 * Component for filtering the [AuthJwtClaims] from the request.
 */
@Component
class TokenFilterComponent(
  private val accessTokenJwtService: AccessTokenJwtService
) {

  /**
   * Filter the access token from the request. If the access token is valid, return the [AuthJwtClaims].
   *
   * @param request The HTTP request
   * @param response The HTTP response
   * @return The claims if the token is valid, null otherwise
   * @see AuthJwtClaims
   */
  fun filter(request: HttpServletRequest, response: HttpServletResponse): AuthJwtClaims? {
    var validClaims: AuthJwtClaims? = null

    try {
      val accessToken = request.getCookieValue(accessTokenJwtService.cookieName)

      if (accessToken != null) {
        validClaims = accessTokenJwtService.extractClaims(accessToken)
      }
    } catch (e: Exception) {
      log.error(e) { "Error while filtering token" }
    }

    return validClaims
  }
}
