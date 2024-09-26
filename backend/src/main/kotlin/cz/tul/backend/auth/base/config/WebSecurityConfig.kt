package cz.tul.backend.auth.base.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

  private val unsecuredEndpoints = arrayOf(
    "/api/v1/welcome/welcome-text"
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
      .build()
  }

  private fun corsConfigurationSource(): CorsConfigurationSource {
    return CorsConfigurationSource {
      CorsConfiguration().apply {
        allowCredentials = true
        allowedOriginPatterns = listOf("*")
        allowedMethods = listOf("*")
        allowedHeaders = listOf("*")
      }
    }
  }
}
