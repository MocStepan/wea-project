import {inject, Injectable} from '@angular/core'

import {HttpService} from '../../shared/http/service/http.service'
import {Observable, tap} from "rxjs";
import {BASE_API_URL} from "../../../config";
import {SignInModel} from "../model/sign-in.model";
import {SignUpModel} from "../model/sign-up.model";
import {HttpHeaders, HttpResponse} from "@angular/common/http";

@Injectable({providedIn: 'root'})
export class AuthService {

  private httpService: HttpService = inject(HttpService)
  private rootHttpUrl = BASE_API_URL + 'auth/'

  signIn(signInForm: SignInModel): Observable<HttpResponse<void>> {
    return this.httpService.post<HttpResponse<void>>(this.rootHttpUrl + 'login', signInForm, {
      headers: new HttpHeaders({'Content-Type': 'application/json'}),
      withCredentials: true
    }).pipe(tap(() => {
      this.setSignedIn();
    }))
  }

  signUp(signUpForm: SignUpModel): Observable<Boolean> {
    return this.httpService.post(this.rootHttpUrl + 'registration', signUpForm)
  }

  isSignedIn() {
    return sessionStorage.getItem('auth') === 'true'
  }

  private setSignedIn() {
    sessionStorage.setItem('auth', `true`)
  }

  signOut() {
    return this.httpService.post(this.rootHttpUrl + 'logout', null,).subscribe(() => {
      sessionStorage.removeItem('auth')
      window.location.reload()
    })
  }
}
