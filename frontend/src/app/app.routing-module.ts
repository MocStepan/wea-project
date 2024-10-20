import {NgModule} from '@angular/core'
import {RouterModule, Routes} from '@angular/router'

import {SignInComponent} from './auth/sign-in/components/sign-in.component'
import {SignUpComponent} from './auth/sing-up/components/sign-up.component'
import {BookDetailComponent} from './book/detail/components/book-detail.component'
import {BookListComponent} from './book/list/components/book-list.component'

const routes: Routes = [
  {path: '', component: BookListComponent},
  {path: 'favorite', component: BookListComponent},
  {path: 'sign-in', component: SignInComponent},
  {path: 'sign-up', component: SignUpComponent},
  {path: 'book-list/:id', component: BookDetailComponent},
  {path: '**', redirectTo: ''}
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
