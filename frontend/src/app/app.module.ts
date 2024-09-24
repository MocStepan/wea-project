import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {FormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {RouterModule} from '@angular/router';
import {BrowserAnimationsModule, provideAnimations} from '@angular/platform-browser/animations';
import {HTTP_INTERCEPTORS, provideHttpClient} from '@angular/common/http';
import {AppRoutingModule} from './app.routing-module';
import {HttpErrorInterceptor} from './shared/http/interceptor/http-error.interceptor';

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
  ],
  providers: [
    provideAnimations(),
    provideHttpClient(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
