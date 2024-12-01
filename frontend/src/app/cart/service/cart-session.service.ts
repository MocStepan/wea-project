import {Injectable} from '@angular/core'

const cartKey = 'cart-key'

/**
 * Service for the cart session.  It allows to add a book to the cart and get the cart.
 */
@Injectable({
  providedIn: 'root'
})
export class CartSessionService {

  /**
   * Add a book to the cart session storage. Store the book id and quantity.
   *
   * @param bookId
   * @param quantity
   */
  addBookToCart(bookId: number, quantity: number): void {
    const cart = JSON.parse(sessionStorage.getItem(cartKey) || '{}')
    cart[bookId] = (cart[bookId] || 0) + quantity
   sessionStorage.setItem(cartKey, JSON.stringify(cart))
  }

  /**
   * Get the cart from the session storage.
   *
   * @returns the cart as a map
   */
  getCart(): Map<number, number> {
    return JSON.parse(sessionStorage.getItem(cartKey) || '{}')
  }

  /**
   * Remove a book from the cart session storage.
   *
   * @param bookId
   */
  removeBookFromCart(bookId: number): void {
    const cart = JSON.parse(sessionStorage.getItem(cartKey) || '{}')
    delete cart[bookId]
    sessionStorage.setItem(cartKey, JSON.stringify(cart))
  }

  /**
   * Clear the cart session storage.
   */
  clearCart(): void {
    sessionStorage.removeItem(cartKey)
  }
}
