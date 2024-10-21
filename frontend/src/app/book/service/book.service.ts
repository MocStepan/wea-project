import {inject, Injectable} from '@angular/core'
import {Observable} from 'rxjs'

import {BASE_API_URL} from '../../../config'
import {OptionViewModel} from '../../shared/filter/model/option-view.model'
import {PageResponseModel} from '../../shared/filter/model/page-response.model'
import {HttpService} from '../../shared/http/service/http.service'
import {BookFilterModel} from '../list/model/book-filter.model'
import {BookTableModel} from '../list/model/book-table.model'

@Injectable({providedIn: 'root'})
// Marks the service as injectable and provided at the root level, making it available as a singleton service across the app.

export class BookService {

  // Injects the HttpService to handle HTTP requests to the backend.
  private httpService: HttpService = inject(HttpService)

  // Method to filter books based on the provided filter criteria (BookFilterModel).
  // Sends a POST request to the 'book/filter' API endpoint with the filterDTO as the request body.
  filterBooks(filterDTO: BookFilterModel): Observable<PageResponseModel<BookTableModel>> {
    return this.httpService.post(`${BASE_API_URL}book/filter`, filterDTO)
  }

  // Method to retrieve the available book categories as option views.
  // Sends a GET request to the 'book/categories' API endpoint and returns an array of OptionViewModel.
  getBookCategoriesOptionViews(): Observable<OptionViewModel[]> {
    return this.httpService.get(`${BASE_API_URL}book/categories`)
  }

  // Method to retrieve the available book authors as option views.
  // Sends a GET request to the 'book/authors' API endpoint and returns an array of OptionViewModel.
  getBookAuthorsOptionViews(): Observable<OptionViewModel[]> {
    return this.httpService.get(`${BASE_API_URL}book/authors`)
  }
}
