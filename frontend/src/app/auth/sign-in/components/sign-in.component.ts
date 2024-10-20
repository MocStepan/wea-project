import {NgIf} from '@angular/common'
import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core'
import {FormBuilder, FormGroup, NG_VALUE_ACCESSOR, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButton} from '@angular/material/button'
import {MatCard, MatCardHeader, MatCardTitle} from '@angular/material/card'
import {MatCheckbox} from '@angular/material/checkbox'
import {MatFormField, MatPrefix} from '@angular/material/form-field'
import {MatIcon} from '@angular/material/icon'
import {MatInput} from '@angular/material/input'
import {MatToolbar} from '@angular/material/toolbar'
import {Router} from '@angular/router'
import {TranslateModule, TranslateService} from '@ngx-translate/core'
import {Subscription} from 'rxjs'

import {NotificationService} from '../../../shared/notification/notification.service'
import {AuthService} from '../../service/auth.service'

@Component({
  selector: 'app-sign-in',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatFormField,
    MatCard,
    MatToolbar,
    MatIcon,
    MatInput,
    MatButton,
    NgIf,
    ReactiveFormsModule,
    MatPrefix,
    MatCardHeader,
    MatCardTitle,
    MatCheckbox,
    TranslateModule
  ],
  templateUrl: 'sign-in.component.html',
  styleUrl: '../../style/auth.component.css',
  providers: [
    AuthService,
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: SignInComponent
    }
  ]
})
export class SignInComponent implements OnInit, OnDestroy {
  protected formGroup!: FormGroup
  private subscriptions: Subscription[] = []
  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private notificationService: NotificationService,
              private translate: TranslateService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.formGroup = this.buildFormGroup()
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe())
  }

  onSubmit() {
    if (this.formGroup.valid) {
      this.subscriptions.push(this.authService.signIn(this.formGroup.value).subscribe({
        next: () => {
          // Fetch localized success message
          this.translate.get('auth.loginSuccess').subscribe((res: string) => {
            this.notificationService.successNotification(res)
          })
          this.router.navigate(['/'])
        },
        error: () => {
          // Fetch localized error message
          this.translate.get('auth.loginError').subscribe((res: string) => {
            this.notificationService.errorNotification(res)
          })
        }
      }))
    } else {
      // Fetch localized form error message
      this.translate.get('auth.formError').subscribe((res: string) => {
        this.notificationService.errorNotification(res)
      })
    }
  }

  openSignUpForm() {
    this.router.navigate(['/sign-up'])
  }

  private buildFormGroup() {
    const form = {
      email: [null, [Validators.required, Validators.email]],
      password: [null, [Validators.required]],
      rememberMe: [false]
    }
    return this.formBuilder.group(form)
  }
}
