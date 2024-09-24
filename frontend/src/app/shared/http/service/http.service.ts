import {HttpClient} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';


@Injectable({providedIn: 'root'})
export class HttpService {

  private httpClient: HttpClient = inject(HttpClient);

  get<T>(url: string, options = {}): Observable<T> {
    return this.httpClient.get<T>(url, options);
  }

  post<T>(url: string, body: any, options = {}): Observable<T> {
    return this.httpClient.post<T>(url, body, options);
  }

  put<T>(url: string, body: any, options = {}): Observable<T> {
    return this.httpClient.put<T>(url, body, options);
  }

  delete<T>(url: string, options = {}): Observable<T> {
    return this.httpClient.delete<T>(url, options);
  }
}
