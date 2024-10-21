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
  protected isUserSignedIn: WritableSignal<boolean> = signal(false)
  protected langFormControl: FormControl = new FormControl<string>('cz')
  protected isChecked = false
  protected lang = 'Čeština'
  protected user: WritableSignal<Nullable<UserModel>> = signal(null)
  private readonly subscriptions: Subscription[] = []
  private currentUrl = ''

  // Injected services and dependencies instead of using the constructor.
  private router: Router = inject(Router)
  private changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef)
  private authService: AuthService = inject(AuthService)
  private userService = inject(UserService)
  private translate: TranslateService = inject(TranslateService)


  /**
   * Initializes the component by setting up the language, current URL, and user details.
   */
  ngOnInit() {
    // Load the saved language from localStorage, defaulting to 'cz' if no language is saved.
    const savedLang = localStorage.getItem('lang') || 'cz'
    this.langFormControl.setValue(savedLang)
    this.translate.use(savedLang)

    this.translate.get(savedLang === 'en' ? 'ENGLISH' : 'CZECH').subscribe((res: string) => {
      this.lang = res
    })

    this.currentUrl = this.router.url
    this.navigationRouter()
    this.isUserSignedIn.set(this.authService.isSignedIn())
    this.getUser()
  }

  /**
   * Cleans up all subscriptions when the component is destroyed. This prevents memory leaks.
   */
  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe())
  }

  /**
   * Checks if the user is signed in, and if so, fetches the user details from the backend and
   * stores them in the session storage.
   */
  getUser(): void {
    if (this.isUserSignedIn()) {
      this.userService.getAuthUser().subscribe((response) => {
        this.user.set(response)
        sessionStorage.setItem('user', JSON.stringify(response))
      })
    }
  }

  /**
   * Checks if the current URL is the same as the navigation URL.
   *
   * @param navigationUrl The URL to compare with the current URL.
   * @returns True if the current URL is the same as the navigation URL, false otherwise.
   */
  isSelected(navigationUrl: string) {
    return this.currentUrl == navigationUrl
  }

  /**
   * Signs out the user by calling the signOut method from the AuthService.
   */
  signOut() {
    this.authService.signOut()
  }

  /**
   * Toggles the language between Czech and English based on the provided isChecked value.
   *
   * @param isChecked The value to determine the language to switch to.
   */
  getLang(isChecked: boolean) {
    const lang = isChecked ? 'en' : 'cz'
    this.translate.use(lang)

    this.translate.get(isChecked ? 'ENGLISH' : 'CZECH').subscribe((res: string) => {
      this.lang = res
    })
    localStorage.setItem('lang', lang)
  }

  /**
   * Manually triggers change detection to update the view when the language is changed.
   */
  private navigationRouter() {
    this.subscriptions.push(this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd)
    ).subscribe((event) => {
      this.isUserSignedIn.set(this.authService.isSignedIn())
      this.currentUrl = (event as NavigationEnd).url
      this.changeDetectorRef.detectChanges()
    }))
  }
}

