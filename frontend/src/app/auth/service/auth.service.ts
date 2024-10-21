import {HttpResponse} from '@angular/common/http'
import {inject, Injectable} from '@angular/core'
import {Observable, tap} from 'rxjs'

import {BASE_API_URL} from '../../../config'
import {HttpService} from '../../shared/http/service/http.service'
import {SignInModel} from '../model/sign-in.model'
import {SignUpModel} from '../model/sign-up.model'

@Injectable({providedIn: 'root'})
// Marks this service as injectable and provided at the root level, making it a singleton service accessible throughout the entire application.

export class AuthService {

  // Dependency injection of the HttpService to make HTTP requests to the backend.
  private httpService: HttpService = inject(HttpService)

  // Base URL for authentication-related API calls.
  private rootHttpUrl = BASE_API_URL + 'auth/'

  // Method to handle user sign-in, sends a POST request with the sign-in form data.
  signIn(signInForm: SignInModel): Observable<HttpResponse<void>> {
    // Sends an HTTP POST request to the login endpoint.
    // Upon a successful response, the user is marked as signed in by invoking setSignedIn().
    return this.httpService.post<HttpResponse<void>>(this.rootHttpUrl + 'login', signInForm).pipe(tap(() => {
      this.setSignedIn()
    }))
  }

  // Method to handle user sign-up, sends a POST request with the sign-up form data.
  signUp(signUpForm: SignUpModel): Observable<boolean> {
    // Sends an HTTP POST request to the registration endpoint.
    return this.httpService.post(this.rootHttpUrl + 'registration', signUpForm)
  }

  // Checks if the user is signed in by checking the value stored in sessionStorage.
  isSignedIn() {
    return sessionStorage.getItem('auth') === 'true'
  }

  // Method to handle user sign-out, sends a POST request to the logout endpoint.
  signOut() {
    // Sends an HTTP POST request to the logout endpoint and clears the session.
    return this.httpService.post(this.rootHttpUrl + 'logout', null).subscribe(() => {
      // Removes the 'auth' session key and reloads the page to reflect the sign-out.
      sessionStorage.removeItem('auth')
      window.location.reload()
    })
  }

  // Marks the user as signed in by setting the 'auth' value in sessionStorage.
  private setSignedIn() {
    sessionStorage.setItem('auth', 'true')
  }
}
