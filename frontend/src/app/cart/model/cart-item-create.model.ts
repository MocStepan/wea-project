
export class CartItemCreateModel {
  bookId: number
  quantity: number

  constructor(bookId: number, quantity: number) {
    this.bookId = bookId
    this.quantity = quantity
  }
}
