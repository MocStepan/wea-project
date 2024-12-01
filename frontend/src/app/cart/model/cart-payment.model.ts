import {FormControl} from '@angular/forms'
import {Nullable} from 'primeng/ts-helpers'

import {PaymentMethodEnum} from '../valueobject/payment-method.enum'

export interface CartPaymentModel {
  email: (string | null)[];
  payingMethod: (string | null)[];
}

export interface CartPaymentFormValue {
  email: Nullable<string>
  paymentMethod: Nullable<string>
}
export interface CartPaymentFormGroup {
  email: FormControl<string | null>;
  paymentMethod: FormControl<PaymentMethodEnum | null>;
}
