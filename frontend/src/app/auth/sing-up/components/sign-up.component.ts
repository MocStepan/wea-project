import {NgIf} from '@angular/common'
import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core'
import {FormBuilder, FormGroup, NG_VALUE_ACCESSOR, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButton} from '@angular/material/button'
import {MatCard, MatCardTitle} from '@angular/material/card'
import {MatError, MatFormField, MatLabel, MatPrefix} from '@angular/material/form-field'
import {MatIcon} from '@angular/material/icon'
import {MatInput} from '@angular/material/input'
import {MatToolbar} from '@angular/material/toolbar'
import {Router} from '@angular/router'
import {TranslateModule, TranslateService} from '@ngx-translate/core'
import {Subscription} from 'rxjs'

import {NotificationService} from '../../../shared/notification/notification.service'
import {AuthService} from '../../service/auth.service'

@Component({
  selector: 'app-sign-up',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatButton,
    MatCard,
    MatFormField,
    MatIcon,
    MatInput,
    MatToolbar,
    NgIf,
    ReactiveFormsModule,
    MatPrefix,
    MatCardTitle,
    MatError,
    MatLabel,
    TranslateModule
  ],
  templateUrl: 'sign-up.component.html',
  styleUrl: '../../style/auth.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: SignUpComponent
    }
  ]
})
export class SignUpComponent implements OnInit, OnDestroy {
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
      this.subscriptions.push(this.authService.signUp(this.formGroup.value).subscribe({
        next: () => {
          this.translate.get('auth.loginSuccess').subscribe((res: string) => {
            this.notificationService.successNotification(res)
          })
          this.router.navigate(['/signIn'])
        },
        error: () => {
          this.translate.get('auth.loginError').subscribe((res: string) => {
            this.notificationService.errorNotification(res)
          })        }
      }))
    } else {
      this.translate.get('auth.formError').subscribe((res: string) => {
        this.notificationService.errorNotification(res)
      })    }
  }

  openSignInForm() {
    this.router.navigate(['/sign-in'])
  }

  private buildFormGroup(): FormGroup {
    return this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.email, Validators.required]],
      password: ['', [Validators.required]],
      secondPassword: ['', [Validators.required]]
    })
  }
}
