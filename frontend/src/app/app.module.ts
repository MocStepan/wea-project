import {HTTP_INTERCEPTORS, provideHttpClient} from '@angular/common/http'
import {NgModule} from '@angular/core'
import {FormsModule} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MatCardModule} from '@angular/material/card'
import {MatIconModule} from '@angular/material/icon'
import {BrowserModule} from '@angular/platform-browser'
import {BrowserAnimationsModule, provideAnimations} from '@angular/platform-browser/animations'
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async'
import {RouterModule} from '@angular/router'

import {AppComponent} from './app.component'
import {AppRoutingModule} from './app.routing-module'
import {HttpErrorInterceptor} from './shared/http/interceptor/http-error.interceptor'
import {HttpHeaderInterceptor} from './shared/http/interceptor/http-header.interceptor'
import {NavigationComponent} from './shared/navigation/navigation.component'

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule,
    BrowserAnimationsModule,
    FormsModule,
    NavigationComponent,
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ],
  providers: [
    provideAnimations(),
    provideHttpClient(),
    provideAnimationsAsync(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpHeaderInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
