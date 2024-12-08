import {PaymentMethodEnum} from '../../cart/valueobject/payment-method.enum'
import {OrderItemModel} from './order-item.model'

export interface OrderModel {
  id: number
  paymentMethod: PaymentMethodEnum
  totalPrice: number
  createdDateTime: Date
  orderItems: OrderItemModel[]
}
