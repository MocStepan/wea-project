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

  // Form group for the sign-in form, used to track form inputs and validation.
  protected formGroup!: FormGroup

  // List of subscriptions to manage and clean up observable subscriptions.
  private subscriptions: Subscription[] = []

  // Constructor that injects necessary services for form handling, authentication, notifications, translations, and routing.
  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private notificationService: NotificationService,
              private translate: TranslateService,
              private router: Router) {
  }

  // Lifecycle hook that is called when the component is initialized.
  ngOnInit(): void {
    // Initializes the form group with the input fields and their validators.
    this.formGroup = this.buildFormGroup()
  }

  // Lifecycle hook that is called when the component is destroyed.
  ngOnDestroy(): void {
    // Unsubscribes from all subscriptions to prevent memory leaks.
    this.subscriptions.forEach((subscription) => subscription.unsubscribe())
  }

  // Method triggered on form submission, handles user sign-in.
  onSubmit() {
    // Checks if the form is valid before proceeding with the sign-in.
    if (this.formGroup.valid) {
      // Calls the signIn method of the AuthService and adds the subscription to the list for cleanup.
      this.subscriptions.push(this.authService.signIn(this.formGroup.value).subscribe({
        next: () => {
          // On successful sign-in, fetches a localized success message and shows a success notification.
          this.translate.get('auth.loginSuccess').subscribe((res: string) => {
            this.notificationService.successNotification(res)
          })
          // Navigates to the home page after successful login.
          this.router.navigate(['/'])
        },
        error: () => {
          // On sign-in failure, fetches a localized error message and shows an error notification.
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

  // Method to navigate to the sign-up form if the user wants to register.
  openSignUpForm() {
    this.router.navigate(['/sign-up'])
  }

  // Private method that builds and returns the form group for the sign-in form, including fields and validators.
  private buildFormGroup() {
    const form = {
      email: [null, [Validators.required, Validators.email]], // Email field with required and email format validators.
      password: [null, [Validators.required]], // Password field with required validator.
      rememberMe: [false] // Remember Me checkbox.
    }
    return this.formBuilder.group(form)
  }
}
