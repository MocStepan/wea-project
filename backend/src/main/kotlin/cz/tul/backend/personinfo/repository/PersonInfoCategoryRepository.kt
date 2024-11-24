package cz.tul.backend.personinfo.repository

import cz.tul.backend.personinfo.entity.PersonInfoCategory
import org.springframework.data.jpa.repository.JpaRepository

interface PersonInfoCategoryRepository : JpaRepository<PersonInfoCategory, Long> {

  fun deleteByPersonInfo_Id(id: Long): Long

  fun existsByPersonInfo_IdAndBookCategory_Name(id: Long, name: String): Boolean
}
