import {NgIf} from '@angular/common'
import {ChangeDetectionStrategy, Component, inject, OnInit, signal, WritableSignal} from '@angular/core'
import {MatButton} from '@angular/material/button'
import {MatStep, MatStepLabel, MatStepper, MatStepperNext, MatStepperPrevious} from '@angular/material/stepper'
import {TranslateModule} from '@ngx-translate/core'

import {BookFilterModel} from '../../book/list/model/book-filter.model'
import {BookTableModel} from '../../book/list/model/book-table.model'
import {BookService} from '../../book/service/book.service'
import {PersonInfoComponent} from '../../person-info/components/person-info.component'
import {FilterCriteriaModel} from '../../shared/filter/model/filter-criteria.model'
import {PageResponseModel} from '../../shared/filter/model/page-response.model'
import {FilterOperatorEnum} from '../../shared/filter/valueobject/filter-operator.enum'
import {CartConfirmationStepComponent} from '../confirmation-step/cart-confirmation-step.component'
import {IntroStepComponent} from '../intro-step/intro-step.component'
import {CartSessionItem} from '../model/cart-session-item.model'
import {CartSessionService} from '../service/cart-session.service'

const CONFIG_NAME = 'cart-main-stepper'

/**
 * Component used as root for the cart stepper.
 */
@Component({
  selector: 'app-cart-main-stepper',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    MatStepper,
    MatStep,
    MatStepLabel,
    IntroStepComponent,
    PersonInfoComponent,
    CartConfirmationStepComponent,
    TranslateModule,
    MatButton,
    MatStepperPrevious,
    MatStepperNext,
    NgIf
  ],
  providers: [PersonInfoComponent],
  templateUrl: './cart-main-stepper.component.html',
  styleUrls: ['../style/cart.component.css']
})
export class CartMainStepperComponent implements OnInit {
  private cartSessionService = inject(CartSessionService)
  private bookService = inject(BookService)

  protected bookFilter: BookFilterModel = BookFilterModel.createDefaultFilter(CONFIG_NAME)

  public cartSessionItems: CartSessionItem[] = []
  public books: WritableSignal<PageResponseModel<BookTableModel> | null> = signal(null)

  public ngOnInit(): void {
    this.cartSessionItems = this.cartSessionService.getCart()
    this.filterBooks()
  }

  /**
   * Filter book by book ids from cart.
   *
   * @private
   */
  private filterBooks() {
    const size = this.cartSessionItems.length
    const ids = this.cartSessionItems.map(item => item.bookId)

    this.bookFilter.size = size
    this.bookFilter.id = new FilterCriteriaModel(FilterOperatorEnum.IN, ids)
    if (size === 0) {
      this.books.set(null)
      return
    }

    this.bookService.filterBooks(this.bookFilter, false).subscribe((response) => {
      this.books.set(response)
    })
  }
}

