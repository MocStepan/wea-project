import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {WelcomeService} from '../service/welcome.service';
import {NotificationService} from '../../shared/notification/notification.service';
import {WelcomeModel} from '../model/welcome.model';
import {Nullable} from '../../shared/utils/shared-types';

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [],
  providers: [],
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent implements OnInit {

  protected welcomeModel: WritableSignal<Nullable<WelcomeModel>> = signal(null);
  private welcomeService: WelcomeService = inject(WelcomeService);
  private notificationService: NotificationService = inject(NotificationService);

  ngOnInit(): void {
    this.welcomeService.getWelcomeText().subscribe({
      next: (response) => {
        this.welcomeModel.set(response);
        this.notificationService.successNotification('Welcome message fetched successfully');
      },
      error: (err) => {
        this.notificationService.errorNotification('Unable to fetch welcome message');
      }
    });
  }
}
