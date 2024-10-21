import {NgIf} from '@angular/common'
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnDestroy,
  OnInit,
  signal,
  WritableSignal
} from '@angular/core'
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms'
import {MatOption} from '@angular/material/autocomplete'
import {MatIcon} from '@angular/material/icon'
import {MatFormField, MatSelect} from '@angular/material/select'
import {MatSlideToggle} from '@angular/material/slide-toggle'
import {MatToolbar} from '@angular/material/toolbar'
import {NavigationEnd, Router, RouterLink} from '@angular/router'
import {TranslateModule, TranslateService} from '@ngx-translate/core'
import {filter, Subscription} from 'rxjs'

import {AuthService} from '../../auth/service/auth.service'
import {UserModel} from '../../user/model/user.model'
import {UserService} from '../../user/service/user.service'
import {Nullable} from '../utils/shared-types'

@Component({
  selector: 'app-navigation',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    TranslateModule,
    MatIcon,
    MatToolbar,
    RouterLink,
    MatOption,
    MatSelect,
    MatFormField,
    ReactiveFormsModule,
    NgIf,
    MatSlideToggle,
    FormsModule
  ],
  providers: [
    Router,
    AuthService
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.css'
})
export class NavigationComponent implements OnInit, OnDestroy {

  // Signal that tracks whether the user is signed in, initialized to 'false'.
  protected isUserSignedIn: WritableSignal<boolean> = signal(false)

  // Form control for managing the selected language, initialized to 'cz' (Czech).
  protected langFormControl: FormControl = new FormControl<string>('cz')

  // Boolean flag to track a checkbox status, used in language switching.
  protected isChecked = false

  // Stores the current language as a string. Initially set to 'Čeština' (Czech).
  protected lang = 'Čeština'

  // Signal that tracks the current authenticated user, initialized to 'null'.
  protected user: WritableSignal<Nullable<UserModel>> = signal(null)

  // Array to store active subscriptions for cleanup during component destruction.
  private readonly subscriptions: Subscription[] = []

  // Stores the current URL of the application.
  private currentUrl = ''

  // Router service to handle navigation and URL changes.
  private router: Router = inject(Router)

  // Service to detect changes and trigger view updates manually.
  private changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef)

  // Authentication service to manage user sign-in status and authentication.
  private authService: AuthService = inject(AuthService)

  // Service to manage user-related actions, such as fetching the authenticated user.
  private userService = inject(UserService)

  constructor(private translate: TranslateService) {
  }

  // Lifecycle hook that runs when the component is initialized.
  ngOnInit() {
    // Load the saved language from localStorage, defaulting to 'cz' if no language is saved.
    const savedLang = localStorage.getItem('lang') || 'cz'

    // Set the form control value to the saved language and apply the translation.
    this.langFormControl.setValue(savedLang)
    this.translate.use(savedLang)

    // Fetch and set the current language label (e.g., 'English' or 'Czech') based on the language.
    this.translate.get(savedLang === 'en' ? 'ENGLISH' : 'CZECH').subscribe((res: string) => {
      this.lang = res
    })

    // Store the current URL.
    this.currentUrl = this.router.url

    // Set up router event listener to track URL changes.
    this.navigationRouter()

    // Check if the user is signed in and update the signal.
    this.isUserSignedIn.set(this.authService.isSignedIn())

    // Fetch and set the authenticated user details if the user is signed in.
    this.getUser()
  }

  // Lifecycle hook that runs when the component is destroyed.
  // Unsubscribes from all active subscriptions to prevent memory leaks.
  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe())
  }

  // Fetches the authenticated user from the UserService if the user is signed in.
  getUser(): void {
    if (this.isUserSignedIn()) {
      this.userService.getAuthUser().subscribe((response) => {
        this.user.set(response)
        sessionStorage.setItem('user', JSON.stringify(response)) // Store user data in sessionStorage.
      })
    }
  }

  // Checks if the current URL matches the provided navigation URL.
  isSelected(navigationUrl: string) {
    return this.currentUrl == navigationUrl
  }

  // Logs out the user by calling the sign-out method from AuthService.
  signOut() {
    this.authService.signOut()
  }

  // Handles language switching based on a checkbox toggle.
  // Changes the language and stores the preference in localStorage.
  getLang(isChecked: boolean) {
    const lang = isChecked ? 'en' : 'cz'
    this.translate.use(lang)

    // Fetch and update the language label (e.g., 'English' or 'Czech').
    this.translate.get(isChecked ? 'ENGLISH' : 'CZECH').subscribe((res: string) => {
      this.lang = res
    })

    // Save the selected language in localStorage.
    localStorage.setItem('lang', lang)
  }

  // Sets up a router event listener to detect URL changes (NavigationEnd event).
  // Updates the sign-in status and the current URL when a navigation event occurs.
  private navigationRouter() {
    this.subscriptions.push(this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd)
    ).subscribe((event) => {
      // Update the sign-in status and current URL when navigation ends.
      this.isUserSignedIn.set(this.authService.isSignedIn())
      this.currentUrl = (event as NavigationEnd).url

      // Manually trigger change detection.
      this.changeDetectorRef.detectChanges()
    }))
  }
}

