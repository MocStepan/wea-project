package cz.tul.backend.personinfo.repository

import cz.tul.backend.personinfo.entity.PersonInfoAddress
import cz.tul.backend.personinfo.valueobject.AddressType
import org.springframework.data.jpa.repository.JpaRepository

interface PersonInfoAddressRepository : JpaRepository<PersonInfoAddress, Long> {

  fun findByPersonInfo_IdAndAddressType(id: Long, addressType: AddressType): PersonInfoAddress?
}
