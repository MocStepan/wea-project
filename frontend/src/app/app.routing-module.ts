import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {WelcomeComponent} from './welcome/components/welcome.component';
import {SignInComponent} from './auth/sign-in/components/sign-in.component';
import {SignUpComponent} from './auth/sing-up/components/sign-up.component';
import {BookListComponent} from './book/list/components/book-list.component';
import {BookDetailComponent} from './book/detail/components/book-detail.component';

const routes: Routes = [
  {path: '', component: WelcomeComponent},
  {path: 'favorite', component: WelcomeComponent},
  {path: 'sing-in', component: SignInComponent},
  {path: 'sing-up', component: SignUpComponent},
  {path: 'book-list', component: BookListComponent},
  {path: 'book-list/:id', component: BookDetailComponent},
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
