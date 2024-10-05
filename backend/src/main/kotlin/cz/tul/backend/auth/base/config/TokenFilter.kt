package cz.tul.backend.auth.base.config

import cz.tul.backend.auth.base.cookie.access.AccessTokenJwtService
import cz.tul.backend.auth.base.dto.AuthJwtClaims
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.util.WebUtils

private val log = KotlinLogging.logger {}

@Component
class TokenFilter(
  private val accessTokenJwtService: AccessTokenJwtService
) {

  fun filter(request: HttpServletRequest, response: HttpServletResponse): AuthJwtClaims? {
    var validClaims: AuthJwtClaims? = null

    try {
      val accessToken = WebUtils.getCookie(request, accessTokenJwtService.cookieName)

      if (accessToken != null) {
        validClaims = accessTokenJwtService.extractClaims(accessToken.toString())
      }
    } catch (e: Exception) {
      log.error(e) { "Error while filtering token" }
    }

    return validClaims
  }
}
