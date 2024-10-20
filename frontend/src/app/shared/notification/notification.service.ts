import {Injectable} from '@angular/core'
import swal from 'sweetalert2'

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  successNotification(successMessage: string): void {
    swal.fire({
      position: 'top-right',
      icon: 'success',
      title: successMessage,
      showConfirmButton: false,
      timer: 3000,
      toast: true
    })
  }

  errorNotification(errorMessage: string): void {
    swal.fire({
      position: 'top-right',
      icon: 'error',
      title: errorMessage,
      showConfirmButton: false,
      timer: 3000,
      toast: true
    })
  }
}
