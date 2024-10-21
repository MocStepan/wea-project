import {HttpResponse} from '@angular/common/http'
import {inject, Injectable} from '@angular/core'
import {Observable, tap} from 'rxjs'

import {BASE_API_URL} from '../../../config'
import {HttpService} from '../../shared/http/service/http.service'
import {SignInModel} from '../model/sign-in.model'
import {SignUpModel} from '../model/sign-up.model'

@Injectable({providedIn: 'root'})
export class AuthService {

  private httpService: HttpService = inject(HttpService)
  private rootHttpUrl = BASE_API_URL + 'auth/'

  signIn(signInForm: SignInModel): Observable<HttpResponse<void>> {
    return this.httpService.post<HttpResponse<void>>(this.rootHttpUrl + 'login', signInForm).pipe(tap(() => {
      this.setSignedIn()
    }))
  }

  signUp(signUpForm: SignUpModel): Observable<boolean> {
    return this.httpService.post(this.rootHttpUrl + 'registration', signUpForm)
  }

  isSignedIn() {
    return sessionStorage.getItem('auth') === 'true'
  }

  signOut() {
    return this.httpService.post(this.rootHttpUrl + 'logout', null).subscribe(() => {
      sessionStorage.removeItem('auth')
      window.location.reload()
    })
  }

  private setSignedIn() {
    sessionStorage.setItem('auth', 'true')
  }
}
