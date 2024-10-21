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

  // Form group for the sign-up form, used to manage input fields and validation rules.
  protected formGroup!: FormGroup

  // Array to store subscriptions to clean them up when the component is destroyed.
  private subscriptions: Subscription[] = []

  // Constructor that injects services needed for form handling, authentication, notifications, translations, and routing.
  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private notificationService: NotificationService,
              private translate: TranslateService,
              private router: Router) {
  }

  // Lifecycle hook called when the component is initialized.
  ngOnInit(): void {
    // Initializes the form group with the necessary fields and validators.
    this.formGroup = this.buildFormGroup()
  }

  // Lifecycle hook called when the component is destroyed.
  ngOnDestroy(): void {
    // Unsubscribes from all subscriptions to prevent memory leaks.
    this.subscriptions.forEach((subscription) => subscription.unsubscribe())
  }

  // Method triggered on form submission, handles user sign-up.
  onSubmit() {
    // Checks if the form is valid before proceeding with sign-up.
    if (this.formGroup.valid) {
      // Calls the signUp method of AuthService and adds the subscription to the list for cleanup.
      this.subscriptions.push(this.authService.signUp(this.formGroup.value).subscribe({
        next: () => {
          // On successful sign-up, fetches a localized success message and shows a success notification.
          this.translate.get('auth.loginSuccess').subscribe((res: string) => {
            this.notificationService.successNotification(res)
          })
          // Navigates to the sign-in page after successful registration.
          this.router.navigate(['/signIn'])
        },
        error: () => {
          // On sign-up failure, fetches a localized error message and shows an error notification.
          this.translate.get('auth.loginError').subscribe((res: string) => {
            this.notificationService.errorNotification(res)
          })
        }
      }))
    } else {
      // If the form is invalid, fetches a localized form error message and shows an error notification.
      this.translate.get('auth.formError').subscribe((res: string) => {
        this.notificationService.errorNotification(res)
      })
    }
  }

  // Method to navigate to the sign-in form if the user already has an account.
  openSignInForm() {
    this.router.navigate(['/sign-in'])
  }

  // Private method to build the form group for the sign-up form, including fields and validators.
  private buildFormGroup(): FormGroup {
    return this.formBuilder.group({
      firstName: ['', [Validators.required]], // First name field with a required validator.
      lastName: ['', [Validators.required]], // Last name field with a required validator.
      email: ['', [Validators.email, Validators.required]], // Email field with required and email format validators.
      password: ['', [Validators.required]], // Password field with a required validator.
      secondPassword: ['', [Validators.required]] // Confirmation password field with a required validator.
    })
  }
}

