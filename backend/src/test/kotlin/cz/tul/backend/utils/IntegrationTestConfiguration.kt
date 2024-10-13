package cz.tul.backend.utils

import com.ninjasquad.springmockk.MockkBean
import org.springframework.context.annotation.Configuration
import org.springframework.test.web.servlet.MockMvc

@Configuration
class IntegrationTestConfiguration {

  @MockkBean(relaxed = true)
  lateinit var mockMvc: MockMvc
}
