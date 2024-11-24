package cz.tul.backend.personinfo.service

import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.personinfo.dto.PersonInfoCategoryDTO
import cz.tul.backend.personinfo.entity.PersonInfo
import cz.tul.backend.personinfo.entity.PersonInfoCategory
import cz.tul.backend.personinfo.repository.PersonInfoCategoryRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class PersonInfoCategoryService(
  private val personInfoCategoryRepository: PersonInfoCategoryRepository,
  private val bookCategoryRepository: BookCategoryRepository
) {

  fun saveCategory(personInfo: PersonInfo, categories: Set<PersonInfoCategoryDTO>?) {
    personInfoCategoryRepository.deleteByPersonInfo_Id(personInfo.id)

    categories?.forEach {
      saveCategory(personInfo, it.name)
    }
  }

  private fun saveCategory(personInfo: PersonInfo, categoryName: String) {
    val bookCategory = bookCategoryRepository.findByName(categoryName)
    if (bookCategory == null) {
      log.warn { "BookCategory not found for name $categoryName" }
      return
    }

    personInfoCategoryRepository.save(PersonInfoCategory(personInfo = personInfo, bookCategory = bookCategory))
  }

  fun getCategory(personId: Long): Set<PersonInfoCategoryDTO> {
    return bookCategoryRepository.findByPersonInfoCategory_PersonInfo_Id(personId).map {
      PersonInfoCategoryDTO(it.name)
    }.toSet()
  }
}
