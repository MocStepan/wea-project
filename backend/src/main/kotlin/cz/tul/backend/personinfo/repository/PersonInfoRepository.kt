package cz.tul.backend.personinfo.repository

import cz.tul.backend.personinfo.entity.PersonInfo
import org.springframework.data.jpa.repository.JpaRepository

interface PersonInfoRepository : JpaRepository<PersonInfo, Long> {

  fun findByAuthUser_Id(id: Long): PersonInfo?

  fun existsByAuthUser_Id(id: Long): Boolean
}
