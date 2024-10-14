package cz.tul.backend.auth.base.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
  private val objectMapper: ObjectMapper,
  private val jwtTokenFilter: JwtTokenFilter,
  @Value("\${auth.cors.frontendUrl}") private val frontendUrl: String
) {
  private val unsecuredEndpoints =
    arrayOf(
      "/api/v1/auth/login",
      "/api/v1/auth/register",
      "/api/v1/auth/logout",
      "/api/v1/auth/invoke-refresh-token",
      "api/*/docs/**",
      "api/v1/book/filter",
      "api/v1/book/import",
      "api/v1/book/categories",
      "api/v1/book/authors"
    )

  @Bean
  fun securityConfig(http: HttpSecurity): SecurityFilterChain {
    return http
      .csrf { it.disable() }
      .cors {
        it.configurationSource(corsConfigurationSource())
      }
      .authorizeHttpRequests {
        it
          .requestMatchers(*unsecuredEndpoints).permitAll()
          .anyRequest().authenticated()
      }
      .sessionManagement {
        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }
      .exceptionHandling {
        it.authenticationEntryPoint(authenticationExceptionHandler)
      }
      .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
      .build()
  }

  private val authenticationExceptionHandler =
    { _: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException ->
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.status = HttpServletResponse.SC_UNAUTHORIZED
      objectMapper.writeValue(response.writer, "${authException.message}")
    }

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
