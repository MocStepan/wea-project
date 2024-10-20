import {inject, Injectable} from '@angular/core'
import {Observable} from 'rxjs'

import {BASE_API_URL} from '../../../config'
import {PageResponseModel} from '../../shared/filter/model/page-response.model'
import {HttpService} from '../../shared/http/service/http.service'
import {BookFilterModel} from '../list/model/book-filter.model'
import {BookTableModel} from '../list/model/book-table.model'

@Injectable({providedIn: 'root'})
export class BookService {

  private httpService: HttpService = inject(HttpService)

  filterBooks(filterDTO: BookFilterModel): Observable<PageResponseModel<BookTableModel>> {
    return this.httpService.post(`${BASE_API_URL}book/filter`, filterDTO)
  }
}
