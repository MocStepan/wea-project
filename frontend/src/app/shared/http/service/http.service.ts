import {HttpClient} from '@angular/common/http'
import {inject, Injectable} from '@angular/core'
import {Observable} from 'rxjs'


@Injectable({providedIn: 'root'})
// Marks the HttpService as injectable and provided at the root level, making it available as a singleton service across the app.
export class HttpService {

  // Injects HttpClient to handle HTTP requests for interacting with backend APIs.
  private httpClient: HttpClient = inject(HttpClient)

  // Generic GET method for making HTTP GET requests.
  // Takes the URL as a required parameter and an optional options object for additional configurations (e.g., headers, query params).
  // Returns an Observable of type T, which represents the expected response type.
  get<T>(url: string, options = {}): Observable<T> {
    return this.httpClient.get<T>(url, options)
  }

  // Generic POST method for making HTTP POST requests.
  // Takes the URL, a request body, and an optional options object.
  // Returns an Observable of type T, representing the response from the server.
  post<T>(url: string, body: any, options = {}): Observable<T> {
    return this.httpClient.post<T>(url, body, options)
  }

  // Generic PUT method for making HTTP PUT requests.
  // Takes the URL, a request body to update resources, and optional options.
  // Returns an Observable of type T, representing the server's response.
  put<T>(url: string, body: any, options = {}): Observable<T> {
    return this.httpClient.put<T>(url, body, options)
  }

  // Generic DELETE method for making HTTP DELETE requests.
  // Takes the URL and optional options.
  // Returns an Observable of type T, representing the response (often used for deletion confirmations).
  delete<T>(url: string, options = {}): Observable<T> {
    return this.httpClient.delete<T>(url, options)
  }
}

