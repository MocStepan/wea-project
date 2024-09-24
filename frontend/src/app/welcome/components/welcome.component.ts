import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {WelcomeService} from '../service/welcome.service';
import {NotificationService} from '../../shared/notification/notification.service';

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [],
  providers: [],
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent implements OnInit {

  protected welcomeMessage: WritableSignal<string> = signal('');
  private welcomeService: WelcomeService = inject(WelcomeService);
  private notificationService: NotificationService = inject(NotificationService);

  ngOnInit(): void {
    this.welcomeService.getWelcomeText().subscribe({
      next: (response) => {
        this.welcomeMessage.set(response);
        this.notificationService.successNotification('Welcome message fetched successfully');
      },
      error: () => {
        this.notificationService.errorNotification('Unable to fetch welcome message');
      }
    });
  }
}
