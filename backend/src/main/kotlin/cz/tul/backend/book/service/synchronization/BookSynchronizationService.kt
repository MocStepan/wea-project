package cz.tul.backend.book.service.synchronization

import cz.tul.backend.book.dto.BookImportDTO

interface BookSynchronizationService {

  fun synchronizeBooks(importedBooks: List<BookImportDTO>)
}
