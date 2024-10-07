import {inject, Injectable} from '@angular/core';
import {HttpService} from '../../shared/http/service/http.service';

@Injectable({providedIn: 'root'})
export class BookService {

  private httpService: HttpService = inject(HttpService);
}
