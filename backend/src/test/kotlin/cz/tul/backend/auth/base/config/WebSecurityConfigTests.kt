package cz.tul.backend.auth.base.config

import cz.tul.backend.IntegrationTestApplication
import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [IntegrationTestApplication::class])
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WebSecurityConfigTests(
  private val mockMvc: MockMvc
) : FunSpec({

  test("unsecured endpoints should be accessible") {
    val unsecuredEndpoints = listOf(
      "/api/v1/welcome/welcome-text"
    )

    unsecuredEndpoints.forEach { endpoint ->
      mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
        .andExpect(MockMvcResultMatchers.status().isOk)
    }
  }

  test("secured endpoints should be unauthorized without authentication") {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/secured-endpoint"))
      .andExpect(MockMvcResultMatchers.status().isUnauthorized)
  }

  test("should return JSON response on unauthorized exception") {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/secured-endpoint"))
      .andExpect(MockMvcResultMatchers.status().isUnauthorized)
      .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
      .andExpect(MockMvcResultMatchers.jsonPath("$").value("Full authentication is required to access this resource"))
  }
})
