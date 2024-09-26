import {inject, Injectable} from '@angular/core';
import {HttpService} from '../../shared/http/service/http.service';
import {Observable} from 'rxjs';
import {WelcomeModel} from '../model/welcome.model';

@Injectable({providedIn: 'root'})
export class WelcomeService {

  private httpService: HttpService = inject(HttpService);

  getWelcomeText(): Observable<WelcomeModel> {
    return this.httpService.get(`http://localhost:8080/api/v1/welcome/welcome-text`);
  }
}
