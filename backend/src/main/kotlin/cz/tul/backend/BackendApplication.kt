package cz.tul.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync
class BackendApplication : SpringBootServletInitializer() {
  override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
    return application.sources(BackendApplication::class.java)
  }
}

fun main(args: Array<String>) {
  runApplication<BackendApplication>(*args)
}
