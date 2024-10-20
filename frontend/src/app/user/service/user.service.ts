import {inject, Injectable} from '@angular/core'
import {Observable} from 'rxjs'

import {BASE_API_URL} from '../../../config'
import {HttpService} from '../../shared/http/service/http.service'
import {UserModel} from '../model/user.model'

@Injectable({providedIn: 'root'})
export class UserService {

  private httpService: HttpService = inject(HttpService)

  getAuthUser(): Observable<UserModel> {
    return this.httpService.get(`${BASE_API_URL}auth/user`)
  }
}
