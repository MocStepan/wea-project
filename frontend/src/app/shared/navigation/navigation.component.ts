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
  protected langFormControl: FormControl = new FormControl<string>('cz') // Initialize empty
  protected isChecked = false
  protected lang = 'Čeština'
  protected user: WritableSignal<Nullable<UserModel>> = signal(null)
  private readonly subscriptions: Subscription[] = []
  private currentUrl = ''
  private router: Router = inject(Router)
  private changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef)
  private authService: AuthService = inject(AuthService)
  private userService = inject(UserService)

  constructor(private translate: TranslateService) {
  }

  ngOnInit() {
    const savedLang = localStorage.getItem('lang') || 'cz'  // Default to 'cz' if not set
    this.langFormControl.setValue(savedLang)
    this.translate.use(savedLang)  // Apply the saved language
    this.translate.get(savedLang === 'en' ? 'ENGLISH' : 'CZECH').subscribe((res: string) => {
      this.lang = res
    })

    this.currentUrl = this.router.url
    this.navigationRouter()
    this.isUserSignedIn.set(this.authService.isSignedIn())
    this.getUser()
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe())
  }

  getUser(): void {
    if (this.isUserSignedIn()) {
      this.userService.getAuthUser().subscribe((response) => {
        this.user.set(response)
        sessionStorage.setItem('user', JSON.stringify(response))  // Store only user data, not the signal
      })
    }
  }

  isSelected(navigationUrl: string) {
    return this.currentUrl == navigationUrl
  }

  signOut() {
    this.authService.signOut()
  }

  getLang(isChecked: boolean) {
    const lang = isChecked ? 'en' : 'cz'
    this.translate.use(lang)
    this.translate.get(isChecked ? 'ENGLISH' : 'CZECH').subscribe((res: string) => {
      this.lang = res
    })
    localStorage.setItem('lang', lang)
  }

  private navigationRouter() {
    this.subscriptions.push(this.router.events.pipe(filter((event) =>
      event instanceof NavigationEnd)
    ).subscribe((event) => {
      this.isUserSignedIn.set(this.authService.isSignedIn())
      this.currentUrl = (event as NavigationEnd).url
      this.changeDetectorRef.detectChanges()
    }))
  }

}

