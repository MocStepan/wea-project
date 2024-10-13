package cz.tul.backend

import cz.tul.backend.utils.IntegrationTestConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(IntegrationTestConfiguration::class)
class IntegrationTestApplication

fun main(args: Array<String>) {
  runApplication<IntegrationTestApplication>(*args)
}
