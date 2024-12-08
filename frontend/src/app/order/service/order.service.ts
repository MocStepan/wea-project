import {inject, Injectable} from '@angular/core'
import {Observable} from 'rxjs'

import {BASE_API_URL} from '../../../config'
import {PageResponseModel} from '../../shared/filter/model/page-response.model'
import {HttpService} from '../../shared/http/service/http.service'
import {OrderFilterModel} from '../model/order.filter.model'
import {OrderModel} from '../model/order.model'

@Injectable({providedIn: 'root'})
export class OrderService {
  private httpService: HttpService = inject(HttpService)

  /**
   * Filters orders based on the provided order filter model.
   *
   * @param orderFilterModel The order filter model containing the filter criteria.
   * @returns An observable of PageResponseModel<OrderModel> containing the filtered orders.
   * @see OrderFilterModel
   * @see PageResponseModel
   * @see OrderModel
   */
  filterOrders(orderFilterModel: OrderFilterModel): Observable<PageResponseModel<OrderModel>> {
    return this.httpService.post(`${BASE_API_URL}cart/filter`, orderFilterModel)
  }
}
