import {CartItemCreateModel} from './cart-item-create.model'
import {PaymentMethodEnum} from '../valueobject/payment-method.enum'

export class CartCreateModel {
  paymentMethod: PaymentMethodEnum
  cartItems: CartItemCreateModel[]

  constructor(paymentMethod: PaymentMethodEnum, cartItems: CartItemCreateModel[]) {
    this.paymentMethod = paymentMethod
    this.cartItems = cartItems
  }
}
