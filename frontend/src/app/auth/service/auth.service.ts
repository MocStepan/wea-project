import {inject, Injectable} from '@angular/core'

import {HttpService} from '../../shared/http/service/http.service'

@Injectable({providedIn: 'root'})
export class AuthService {

  private httpService: HttpService = inject(HttpService)
}
