package cz.tul.backend.common.accesslog

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Configuration
class RequestLoggingFilterConfig {

  @Bean
  fun logFilter(): CommonsRequestLoggingFilter {
    val filter = CommonsRequestLoggingFilter()
    filter.setBeforeMessagePrefix("Incoming Request: ")
    filter.setIncludeQueryString(true)
    filter.setIncludeHeaders(true)
    filter.setIncludeClientInfo(true)
    filter.setIncludePayload(false)
    return filter
  }
}
