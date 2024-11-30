import {NgModule} from '@angular/core'
import {RouterModule, Routes} from '@angular/router'

import {SignInComponent} from './auth/sign-in/components/sign-in.component'
import {SignUpComponent} from './auth/sing-up/components/sign-up.component'
import {BookDetailComponent} from './book/detail/components/book-detail.component'
import {BookListComponent} from './book/list/components/book-list.component'
import {CartComponent} from './cart/components/cart.component'
import {PersonInfoComponent} from './person/person-info/components/person-info.component'

const routes: Routes = [
  {path: '', component: BookListComponent, data: {favorite: false}},
  {path: 'favorite', component: BookListComponent, data: {favorite: true}},
  {path: 'sign-in', component: SignInComponent},
  {path: 'sign-up', component: SignUpComponent},
  {path: 'person-info', component: PersonInfoComponent},
  {path: 'book-list/:id', component: BookDetailComponent},
  {path: 'cart', component: CartComponent},
  {path: '**', redirectTo: ''}

]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
