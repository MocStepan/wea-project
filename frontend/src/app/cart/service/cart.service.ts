import {inject, Injectable} from '@angular/core'
import {Observable} from 'rxjs'

import {BASE_API_URL} from '../../../config'
import {HttpService} from '../../shared/http/service/http.service'
import {CartCreateModel} from '../model/cart-create.model'

/**
 * Service for the cart API.
 */
@Injectable({
  providedIn: 'root'
})
export class CartService {

  private httpService: HttpService = inject(HttpService)

  postCart(cartCreate: CartCreateModel): Observable<boolean> {
    return this.httpService.post(`${BASE_API_URL}cart/`, {cartCreate})
  }
}
