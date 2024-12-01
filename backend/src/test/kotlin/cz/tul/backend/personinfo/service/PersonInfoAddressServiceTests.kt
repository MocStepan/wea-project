package cz.tul.backend.personinfo.service

import cz.tul.backend.personinfo.dto.PersonInfoAddressDTO
import cz.tul.backend.personinfo.entity.PersonInfoAddress
import cz.tul.backend.personinfo.repository.PersonInfoAddressRepository
import cz.tul.backend.personinfo.valueobject.AddressType
import cz.tul.backend.utils.createPersonInfo
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class PersonInfoAddressServiceTests : FeatureSpec({

  feature("save address") {
    scenario("success") {
      val spec = getSpec()

      val personInfoAddressDTO = PersonInfoAddressDTO()
      val personInfo = createPersonInfo()

      val addressSlot = slot<PersonInfoAddress>()

      every { spec.personInfoAddressRepository.save(capture(addressSlot)) } answers { firstArg() }

      spec.personInfoAddressService.saveAddress(personInfoAddressDTO, personInfo, AddressType.BILLING)

      val captured = addressSlot.captured
      captured.personInfo shouldBe personInfo
      captured.addressType shouldBe AddressType.BILLING
      captured.country shouldBe personInfoAddressDTO.country
      captured.city shouldBe personInfoAddressDTO.city
      captured.street shouldBe personInfoAddressDTO.street
      captured.houseNumber shouldBe personInfoAddressDTO.houseNumber
      captured.zipCode shouldBe personInfoAddressDTO.zipCode
    }
  }

  feature("update address") {
    scenario("success") {
      val spec = getSpec()

      val personInfoAddressDTO = PersonInfoAddressDTO()
      val personInfo = createPersonInfo()
      val address = PersonInfoAddress.from(personInfoAddressDTO, personInfo, AddressType.BILLING)

      val addressSlot = slot<PersonInfoAddress>()

      every {
        spec.personInfoAddressRepository.findByPersonInfo_IdAndAddressType(
          personInfo.id,
          AddressType.BILLING
        )
      } returns address
      every { spec.personInfoAddressRepository.save(capture(addressSlot)) } answers { firstArg() }

      spec.personInfoAddressService.updateAddress(personInfoAddressDTO, personInfo, AddressType.BILLING)

      val captured = addressSlot.captured
      captured.personInfo shouldBe personInfo
      captured.addressType shouldBe AddressType.BILLING
      captured.country shouldBe personInfoAddressDTO.country
      captured.city shouldBe personInfoAddressDTO.city
      captured.street shouldBe personInfoAddressDTO.street
      captured.houseNumber shouldBe personInfoAddressDTO.houseNumber
      captured.zipCode shouldBe personInfoAddressDTO.zipCode
    }

    scenario("address not found, create new one") {
      val spec = getSpec()

      val personInfoAddressDTO = PersonInfoAddressDTO()
      val personInfo = createPersonInfo()

      val addressSlot = slot<PersonInfoAddress>()

      every {
        spec.personInfoAddressRepository.findByPersonInfo_IdAndAddressType(
          personInfo.id,
          AddressType.BILLING
        )
      } returns null
      every { spec.personInfoAddressRepository.save(capture(addressSlot)) } answers { firstArg() }

      spec.personInfoAddressService.updateAddress(personInfoAddressDTO, personInfo, AddressType.BILLING)

      val captured = addressSlot.captured
      captured.personInfo shouldBe personInfo
      captured.addressType shouldBe AddressType.BILLING
      captured.country shouldBe personInfoAddressDTO.country
    }
  }

  feature("get billing address by person info id") {
    scenario("success") {
      val spec = getSpec()

      val personInfo = createPersonInfo()
      val address = PersonInfoAddress.from(PersonInfoAddressDTO(), personInfo, AddressType.BILLING)

      every {
        spec.personInfoAddressRepository.findByPersonInfo_IdAndAddressType(
          personInfo.id,
          AddressType.BILLING
        )
      } returns address

      val result = spec.personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfo.id)

      result shouldBe address
    }

    scenario("address not found") {
      val spec = getSpec()

      val personInfo = createPersonInfo()

      every {
        spec.personInfoAddressRepository.findByPersonInfo_IdAndAddressType(
          personInfo.id,
          AddressType.BILLING
        )
      } returns null

      val result = spec.personInfoAddressService.getRefernceIfExistsByPersonInfoId(personInfo.id)

      result shouldBe null
    }
  }
})

private class PersonInfoAddressServiceSpecWrapper(
  val personInfoAddressRepository: PersonInfoAddressRepository
) {
  val personInfoAddressService = PersonInfoAddressService(personInfoAddressRepository)
}

private fun getSpec() = PersonInfoAddressServiceSpecWrapper(mockk())
