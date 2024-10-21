import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http'
import {inject, Injectable} from '@angular/core'
import {catchError, Observable, throwError} from 'rxjs'

import {AuthService} from '../../../auth/service/auth.service'
import {NotificationService} from '../../notification/notification.service'

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  private notificationService = inject(NotificationService)
  private authService = inject(AuthService)

  public intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status == 401) {
          this.authService.signOut()
          this.notificationService.errorNotification('You do not have access to this feature, please login')
          //sessionStorage.removeItem('auth')
          //this.router.navigate(['/signIn'])
        }
        return throwError(() => error)
      })
    )
  }
}
