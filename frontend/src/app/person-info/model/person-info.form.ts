import {FormControl, FormGroup} from '@angular/forms'
import {Nullable} from 'primeng/ts-helpers'

import {PersonInfoModel} from './person-info.model'
import {PersonInfoAddressFormGroup, PersonInfoAddressFormValue} from './person-info-address.form'

export interface PersonInfoForm {
  gender: unknown
  birthDate: unknown
  favoriteCategory: unknown
  referenceSource: unknown
  processingConsent: unknown
  personalAddress: unknown
  billingAddress: unknown
}

export interface PersonInfoFormValue {
  gender: Nullable<string>
  birthDate: Nullable<Date>
  favoriteCategory: Nullable<string>
  referenceSource: Nullable<string>
  processingConsent: Nullable<boolean>
  personalAddress: Nullable<PersonInfoAddressFormValue>
  billingAddress: Nullable<PersonInfoAddressFormValue>
}

export function PersonInfoFormValue(model: PersonInfoModel): PersonInfoFormValue {
  return {
    gender: model.gender,
    birthDate: model.birthDate,
    favoriteCategory: model.favoriteCategory,
    referenceSource: model.referenceSource,
    processingConsent: model.processingConsent,
    personalAddress: model.personalAddress ? PersonInfoAddressFormValue(model.personalAddress) : null,
    billingAddress: model.billingAddress ? PersonInfoAddressFormValue(model.billingAddress) : null
  }
}

export interface PersonInfoFormGroup {
  gender: FormControl<Nullable<string>>
  birthDate: FormControl<Nullable<Date>>
  favoriteCategory: FormControl<Nullable<string>>
  referenceSource: FormControl<Nullable<string>>
  processingConsent: FormControl<Nullable<boolean>>
  personalAddress: FormGroup<Nullable<PersonInfoAddressFormGroup>>
  billingAddress: FormGroup<Nullable<PersonInfoAddressFormGroup>>
}
