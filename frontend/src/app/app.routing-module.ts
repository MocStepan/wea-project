import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {WelcomeComponent} from './welcome/components/welcome.component';

const routes: Routes = [
  {path: '', component: WelcomeComponent},
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
})
export class AppRoutingModule {
}
