package cz.tul.backend.auth.base.cookie.utils

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.web.util.WebUtils

fun HttpServletResponse.addCookie(cookie: ResponseCookie) {
  this.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
}

fun HttpServletRequest.getCookieValue(name: String): String? {
  return WebUtils.getCookie(this, name)?.value
}
