import {inject, Injectable} from '@angular/core';
import {HttpService} from '../../shared/http/service/http.service';
import {Observable} from 'rxjs';
import {WelcomeModel} from '../model/welcome.model';
import {BASE_API_URL} from "../../../config";

@Injectable({providedIn: 'root'})
export class WelcomeService {

  private httpService: HttpService = inject(HttpService);
  getWelcomeText(): Observable<WelcomeModel> {
    return this.httpService.get(BASE_API_URL+`welcome/welcome-text`);
  }
}
