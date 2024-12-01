import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core'

import {CartSessionService} from '../service/cart-session.service'
import {MatStep, MatStepLabel, MatStepper} from '@angular/material/stepper'
import {PersonInfoComponent} from '../../person-info/components/person-info.component'

/**
 * Component for the cart.
 */
@Component({
  selector: 'app-cart',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    MatStepper,
    MatStep,
    MatStepLabel,
    PersonInfoComponent
  ],
  providers: [],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  private cartSessionService = inject(CartSessionService)

  protected cart = new Map<number, number>()

  public ngOnInit(): void {
    this.cart = this.cartSessionService.getCart()
  }
}

