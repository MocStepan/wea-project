import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnDestroy,
  OnInit,
  signal,
  WritableSignal
} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {MatToolbar} from '@angular/material/toolbar';
import {NavigationEnd, Router, RouterLink} from '@angular/router';
import {NgIf} from '@angular/common';
import {filter, Subscription} from 'rxjs';

@Component({
  selector: 'app-navigation',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatIcon,
    MatToolbar,
    RouterLink,
    NgIf
  ],
  providers: [
    Router,
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.css'
})
export class NavigationComponent implements OnInit, OnDestroy {
  protected isUserSignedIn: WritableSignal<boolean> = signal(false);
  private readonly subscriptions: Subscription[] = [];
  private currentUrl: string = '';

  private router: Router = inject(Router);
  private changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);

  ngOnInit() {
    this.currentUrl = this.router.url;
    this.navigationRouter();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

  isSelected(navigationUrl: string) {
    return this.currentUrl == navigationUrl;
  }

  signOut() {

  }

  private navigationRouter() {
    this.subscriptions.push(this.router.events.pipe(filter((event) =>
      event instanceof NavigationEnd)
    ).subscribe((event) => {
      this.currentUrl = (event as NavigationEnd).url;
      this.changeDetectorRef.detectChanges();
    }));
  }
}
