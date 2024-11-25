import {Injectable} from '@angular/core'

const cartKey = 'cart-key'

@Injectable({
  providedIn: 'root'
})
export class CartSessionService {

  addBookToCart(bookId: number, quantity: number): void {
    const cart = JSON.parse(sessionStorage.getItem(cartKey) || '{}')
    cart[bookId] = (cart[bookId] || 0) + quantity
   sessionStorage.setItem(cartKey, JSON.stringify(cart))
  }
}
