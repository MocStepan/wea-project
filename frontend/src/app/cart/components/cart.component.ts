import {ChangeDetectionStrategy, Component} from '@angular/core'

/**
 * Component for the cart.
 */
@Component({
  selector: 'app-cart',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
  ],
  providers: [],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent {

}

