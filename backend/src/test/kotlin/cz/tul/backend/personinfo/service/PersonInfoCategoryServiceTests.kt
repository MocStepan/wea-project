package cz.tul.backend.personinfo.service

import cz.tul.backend.book.entity.BookCategory
import cz.tul.backend.book.repository.BookCategoryRepository
import cz.tul.backend.personinfo.dto.PersonInfoCategoryDTO
import cz.tul.backend.personinfo.entity.PersonInfoCategory
import cz.tul.backend.personinfo.repository.PersonInfoCategoryRepository
import cz.tul.backend.utils.createPersonInfo
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class PersonInfoCategoryServiceTests : FeatureSpec({

  feature("save category") {
    scenario("success") {
      val spec = getSpec()

      val personInfo = createPersonInfo()
      val bookCategory = BookCategory(name = "test")
      val categories = setOf(PersonInfoCategoryDTO("test"))

      val personInfoCategorySlot = slot<PersonInfoCategory>()

      every { spec.personInfoCategoryRepository.deleteByPersonInfo_Id(personInfo.id) } answers { firstArg() }
      every { spec.bookCategoryRepository.findByName("test") } returns bookCategory
      every { spec.personInfoCategoryRepository.save(capture(personInfoCategorySlot)) } answers { firstArg() }

      spec.personInfoCategoryService.saveCategory(personInfo, categories)

      val personInfoCategory = personInfoCategorySlot.captured
      personInfoCategory.personInfo shouldBe personInfo
      personInfoCategory.bookCategory shouldBe bookCategory
    }

    scenario("category not found") {
      val spec = getSpec()

      val personInfo = createPersonInfo()
      val categories = setOf(PersonInfoCategoryDTO("test"))

      every { spec.personInfoCategoryRepository.deleteByPersonInfo_Id(personInfo.id) } answers { firstArg() }
      every { spec.bookCategoryRepository.findByName("test") } returns null

      spec.personInfoCategoryService.saveCategory(personInfo, categories)
    }

    scenario("no categories") {
      val spec = getSpec()

      val personInfo = createPersonInfo()

      every { spec.personInfoCategoryRepository.deleteByPersonInfo_Id(personInfo.id) } answers { firstArg() }

      spec.personInfoCategoryService.saveCategory(personInfo, null)
    }
  }

  feature("get category") {
    scenario("success") {
      val spec = getSpec()

      val personId = 1L
      val bookCategory = BookCategory(name = "test")

      every { spec.bookCategoryRepository.findByPersonInfoCategory_PersonInfo_Id(personId) } returns setOf(bookCategory)

      val response = spec.personInfoCategoryService.getCategory(personId)

      response shouldBe setOf(PersonInfoCategoryDTO("test"))
    }
  }
})

private class PersonInfoCategoryServiceSpecWrapper(
  val personInfoCategoryRepository: PersonInfoCategoryRepository,
  val bookCategoryRepository: BookCategoryRepository
) {
  val personInfoCategoryService = PersonInfoCategoryService(
    personInfoCategoryRepository,
    bookCategoryRepository
  )
}

private fun getSpec() = PersonInfoCategoryServiceSpecWrapper(mockk(), mockk())
