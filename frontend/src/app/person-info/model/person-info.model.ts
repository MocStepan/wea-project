import {Nullable} from 'primeng/ts-helpers'

import {PersonInfoFormValue} from './person-info.form'
import {PersonInfoAddressModel} from './person-info-address.model'

export interface PersonInfoModel {
  gender: Nullable<string>
  birthDate: Nullable<Date>
  favoriteCategory: Nullable<string>
  referenceSource: Nullable<string>
  processingConsent: Nullable<boolean>
  personalAddress: Nullable<PersonInfoAddressModel>
  billingAddress: Nullable<PersonInfoAddressModel>
}

export function PersonInfoModel(formValue: PersonInfoFormValue): PersonInfoModel {
  return {
    gender: formValue.gender,
    birthDate: formValue.birthDate,
    favoriteCategory: formValue.favoriteCategory,
    referenceSource: formValue.referenceSource,
    processingConsent: formValue.processingConsent,
    personalAddress: formValue.personalAddress,
    billingAddress: formValue.billingAddress
  }
}


