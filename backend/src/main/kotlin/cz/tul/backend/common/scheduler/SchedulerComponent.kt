package cz.tul.backend.common.scheduler

import cz.tul.backend.book.service.BookImportSynchronizationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class SchedulerComponent(
  @Value("\${synchronization.book.import.enabled}") private val bookImportEnabled: Boolean,
  private val bookImportSynchronizationService: BookImportSynchronizationService
) {

  @Scheduled(
    cron = "\${synchronization.book.import.cron}",
    zone = "CET"
  )
  @Async
  fun synchronizeBooks() {
    if (bookImportEnabled) {
      log.debug { "Synchronizing books from import table" }
      bookImportSynchronizationService.synchronizeBooks()
    } else {
      log.debug { "Book import synchronization would run by now" }
    }
  }
}
