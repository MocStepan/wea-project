package cz.tul.backend.auth.base.config

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.auth.base.valueobject.AuthRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource

/**
 * Configuration class for Spring Security (cors, csrf, session management, etc.).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class WebSecurityConfig(
  private val objectMapper: ObjectMapper,
  private val jwtTokenFilterService: JwtTokenFilterService,
  @Value("\${auth.cors.frontendUrl}") private val frontendUrl: String
) {

  /**
   * List of unsecured endpoints.
   */
  private val unsecuredEndpoints =
    arrayOf(
      "/api/v1/auth/login",
      "/api/v1/auth/registration",
      "/api/v1/auth/logout",
      "/api/v1/auth/invoke-refresh-token",
      "/api/*/docs/**",
      "/api/v1/book/filter",
      "/api/v1/book/import",
      "/api/v1/book/categories",
      "/api/v1/book/authors",
      "/api/v1/book/*"
    )

  /**
   * Security configuration.
   *
   * @param http HttpSecurity
   */
  @Bean
  fun securityConfig(http: HttpSecurity): SecurityFilterChain {
    return http
      .csrf { it.disable() }
      .cors {
        it.configurationSource(corsConfigurationSource())
      }
      .sessionManagement {
        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }
      .requestCache { config: RequestCacheConfigurer<HttpSecurity> ->
        // get rid of default session cache in favor of cookie cache
        config.disable()
      }
      .authorizeHttpRequests {
        it
          .requestMatchers(*unsecuredEndpoints).permitAll()
          .anyRequest().hasAnyAuthority(*AuthRole.allAuthorities.toTypedArray())
      }
      .exceptionHandling {
        it.authenticationEntryPoint(authenticationExceptionHandler)
      }
      .addFilterBefore(jwtTokenFilterService, UsernamePasswordAuthenticationFilter::class.java)
      .build()
  }

  /**
   * Exception handler for authentication exceptions.
   *
   * @return message with exception
   */
  private val authenticationExceptionHandler =
    { _: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException ->
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.status = HttpServletResponse.SC_UNAUTHORIZED
      objectMapper.writeValue(response.writer, "${authException.message}")
    }

  /**
   * Configuration source for CORS.
   */
  private fun corsConfigurationSource(): CorsConfigurationSource {
    return CorsConfigurationSource {
      CorsConfiguration().apply {
        allowCredentials = true
        allowedOriginPatterns = listOf("*")
        // allowedOrigins = listOf(frontendUrl) we will use it in production
        allowedMethods = listOf("*")
        allowedHeaders = listOf("*")
      }
    }
  }
}
