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
import {FormControl, ReactiveFormsModule} from '@angular/forms'
import {MatOption} from '@angular/material/autocomplete'
import {MatIcon} from '@angular/material/icon'
import {MatFormField, MatSelect} from '@angular/material/select'
import {MatToolbar} from '@angular/material/toolbar'
import {NavigationEnd, Router, RouterLink} from '@angular/router'
import {TranslateModule, TranslateService} from '@ngx-translate/core'
import {filter, Subscription} from 'rxjs'

import {AuthService} from '../../auth/service/auth.service'

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
    NgIf
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
  private readonly subscriptions: Subscription[] = []
  private currentUrl = ''
  private router: Router = inject(Router)
  private changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef)
  protected langFormControl: FormControl = new FormControl<string>('cz')
  private authService: AuthService = inject(AuthService)

  constructor(private translate: TranslateService) {
    this.translate.setDefaultLang(this.langFormControl.value)
  }

  changeLanguage(lang: string) {
    this.translate.use(lang)
  }

  ngOnInit() {
    this.currentUrl = this.router.url
    this.navigationRouter()
    this.isUserSignedIn.set(this.authService.isSignedIn())
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe())
  }

  isSelected(navigationUrl: string) {
    return this.currentUrl == navigationUrl
  }

  private navigationRouter() {
    this.subscriptions.push(this.router.events.pipe(filter((event) =>
      event instanceof NavigationEnd)
    ).subscribe((event) => {
      this.currentUrl = (event as NavigationEnd).url
      this.changeDetectorRef.detectChanges()
    }))
  }
}
