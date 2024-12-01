import {NgForOf, NgIf} from '@angular/common'
import {ChangeDetectionStrategy, Component, inject, OnInit, signal, WritableSignal} from '@angular/core'
import {FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatOption} from '@angular/material/autocomplete'
import {MatButton} from '@angular/material/button'
import {
  MatCard, MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardImage,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card'
import {MatCheckbox} from '@angular/material/checkbox'
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker'
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field'
import {MatGridList, MatGridTile} from '@angular/material/grid-list'
import {MatIcon} from '@angular/material/icon'
import {MatInput} from '@angular/material/input'
import {MatList, MatListItem} from '@angular/material/list'
import {MatSelect} from '@angular/material/select'
import {MatStep, MatStepLabel, MatStepper} from '@angular/material/stepper'
import {TranslateModule, TranslateService} from '@ngx-translate/core'

import {BookListComponent} from '../../book/list/components/book-list.component'
import {BookFilterModel} from '../../book/list/model/book-filter.model'
import {BookTableModel} from '../../book/list/model/book-table.model'
import {BookService} from '../../book/service/book.service'
import {PersonInfoComponent} from '../../person-info/components/person-info.component'
import {FilterCriteriaModel} from '../../shared/filter/model/filter-criteria.model'
import {PageResponseModel} from '../../shared/filter/model/page-response.model'
import {FilterOperatorEnum} from '../../shared/filter/valueobject/filter-operator.enum'
import {NotificationService} from '../../shared/notification/notification.service'
import {CartCreateModel} from '../model/cart-create.model'
import {CartItemCreateModel} from '../model/cart-item-create.model'
import {CartPaymentFormGroup} from '../model/cart-payment.model'
import {CartService} from '../service/cart.service'
import {CartSessionService} from '../service/cart-session.service'
import {PaymentMethodEnum} from '../valueobject/payment-method.enum'

const CONFIG_NAME = 'book-list'

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
    PersonInfoComponent,
    MatButton,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardImage,
    MatCardSubtitle,
    MatCardTitle,
    MatGridList,
    MatGridTile,
    MatIcon,
    NgForOf,
    TranslateModule,
    FormsModule,
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatFormField,
    MatInput,
    MatLabel,
    MatSuffix,
    MatCardActions,
    MatCheckbox,
    MatOption,
    MatSelect,
    ReactiveFormsModule,
    MatList,
    MatListItem,
    NgIf
  ],
  providers: [BookListComponent, PersonInfoComponent, FormBuilder],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  private cartService = inject(CartService)
  private cartSessionService = inject(CartSessionService)
  private bookService = inject(BookService)
  private translateService = inject(TranslateService)
  private notificationService = inject(NotificationService)
  private formBuilder: FormBuilder = inject(FormBuilder)
  bookComponent = inject(BookListComponent)

  protected fee = 0
  cart = new Map<number, number>()
  protected formGroup!: FormGroup<CartPaymentFormGroup>
  protected bookFilter: BookFilterModel = BookFilterModel.createDefaultFilter(CONFIG_NAME)
  protected books: WritableSignal<PageResponseModel<BookTableModel> | null> = signal(null)

  public ngOnInit(): void {
    this.cart = this.cartSessionService.getCart()
    this.cart = new Map(Object.entries(this.cartSessionService.getCart()).map(([key, value]) => [Number(key), value]))
    this.formGroup = this.buildFormGroup()
    this.bookFilter.size = this.cart.size
    this.bookFilter.id = new FilterCriteriaModel(FilterOperatorEnum.IN, Array.from(this.cart.keys()))
    this.filterBooks()
  }
  filterBooks() {
    this.bookService.filterBooks(this.bookFilter, false).subscribe((response) => {
      this.books.set(response)
    })
  }
  removeBookFromCart(id: number) {
    this.cartSessionService.removeBookFromCart(id)
    this.books()!.content = this.books()!.content.filter(book => book.id !== id)
    this.filterBooks()
  }

  goToBookDetail(id: number) {
    this.bookComponent.goToBookDetail(id)
  }

  onSubmit() {
    this.cartService.postCart(new CartCreateModel(this.formGroup.value.paymentMethod!, this.createCartList())).subscribe({
      next: () => {
        this.translateService.get('PAYMENT_SUCCESS').subscribe((res: string) => {
          this.notificationService.successNotification(res)
        })
        this.cartSessionService.clearCart()
      },
      error: () => {
        this.translateService.get('PAYMENT_ERROR').subscribe((res: string) => {
          this.notificationService.errorNotification(res)
        })
      }
    })
  }

  private buildFormGroup(): FormGroup<CartPaymentFormGroup> {
    const validator = [Validators.required]
    return this.formBuilder.group<CartPaymentFormGroup>({
      email: new FormControl<string | null>(null, validator),
      paymentMethod: new FormControl<PaymentMethodEnum | null>(null, validator),
    })
  }

  private createCartList() {
    return Array.from(this.cart.entries()).map(([bookId, quantity]) => new CartItemCreateModel(bookId, quantity))
  }


  getTotalPrice() {
    const total = Array.from(this.cart.entries()).reduce((acc, [bookId, quantity]) => {
      const book = this.books()!.content.find(cost => cost.id === bookId)
      return acc + (book ? book.price * quantity : 0)
    }, 0)
    return total + this.fee
  }
  getEntries() {
    const ids = Array.from(this.cart.entries())
    return ids.map(([bookId, quantity]) => {
      const entry = this.books()?.content.find(book => book.id === bookId)
      return {
        bookId,
        quantity,
        title: entry?.title,
        cost: entry?.price,
      }
    })
  }

  getFee() {
    if (this.formGroup.value.paymentMethod === PaymentMethodEnum.CARD) {
      const entries = this.getEntries()
      if (entries.length === 0) {
        return 0
      }
      this.fee = entries.reduce((acc, entry) => acc + (entry.quantity * entry.cost! * 0.01), 0)
    }
    else if (this.formGroup.value.paymentMethod === PaymentMethodEnum.CASH) {
      this.fee = 50
    }
    else {
      this.fee = 0
    }
    return this.fee
  }

  protected readonly PaymentMethodEnum = PaymentMethodEnum
}

