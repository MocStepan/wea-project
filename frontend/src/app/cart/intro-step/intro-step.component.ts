import {NgForOf, NgIf} from '@angular/common'
import {ChangeDetectionStrategy, Component, inject, Input, signal, WritableSignal} from '@angular/core'
import {MatButton} from '@angular/material/button'
import {MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle} from '@angular/material/card'
import {MatGridList, MatGridTile} from '@angular/material/grid-list'
import {MatIcon} from '@angular/material/icon'
import {Router} from '@angular/router'
import {TranslateModule} from '@ngx-translate/core'

import {BookTableModel} from '../../book/list/model/book-table.model'
import {PageResponseModel} from '../../shared/filter/model/page-response.model'
import {CartSessionItem} from '../model/cart-session-item.model'
import {CartSessionService} from '../service/cart-session.service'

/**
 * Component for the cart first step.
 */
@Component({
  selector: 'app-cart-intro-step',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    MatGridList,
    MatGridTile,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardSubtitle,
    MatCardContent,
    TranslateModule,
    MatIcon,
    MatButton,
    NgForOf,
    NgIf
  ],
  providers: [],
  templateUrl: './intro-step.component.html',
  styleUrls: ['../style/cart.component.css']
})
export class IntroStepComponent {
  @Input({required: true}) public books: WritableSignal<PageResponseModel<BookTableModel> | null> = signal(null)
  @Input({required: true}) public cartSessionItems: CartSessionItem[] = []

  private cartSessionService = inject(CartSessionService)
  private router = inject(Router)

  /**
   * Remove book from cart and update the cart session items.
   * @param id
   * @protected
   */
  protected removeBookFromCart(id: number) {
    this.cartSessionService.removeBookFromCart(id)
    this.cartSessionItems = this.cartSessionService.getCart()

    if (this.books !== null) {
      this.books()!.content = this.books()!.content.filter(book => book.id !== id)
    }
  }

  /**
   * Navigate to the book detail page.
   *
   * @param id
   * @protected
   */
  protected goToBookDetail(id: number) {
    this.router.navigate([`/book-list/${id}`])
  }

  protected getBookQuantity(bookId: number): number {
    return this.cartSessionItems.find(item => item.bookId === bookId)?.quantity ?? 0
  }
}

