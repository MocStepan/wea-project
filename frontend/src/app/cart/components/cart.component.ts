import {NgForOf, NgIf} from '@angular/common'
import {ChangeDetectionStrategy, Component, inject, OnInit, signal, WritableSignal} from '@angular/core'
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatOption} from '@angular/material/autocomplete'
import {MatButton} from '@angular/material/button'
import {
  MatCard,
  MatCardActions,
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
import {MatStep, MatStepLabel, MatStepper, MatStepperNext, MatStepperPrevious} from '@angular/material/stepper'
import {Router} from '@angular/router'
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
import {CartForm, CartFormGroup} from '../model/cart.form'
import {CartCreateModel} from '../model/cart-create.model'
import {CartItemCreateModel} from '../model/cart-item-create.model'
import {CartService} from '../service/cart.service'
import {CartSessionService} from '../service/cart-session.service'
import {addPaymentMethodToPrice, PaymentMethodEnum} from '../valueobject/payment-method.enum'

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
    NgIf,
    MatStepperNext,
    MatStepperPrevious
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
  private router = inject(Router)

  protected booksTotalPrice = 0
  protected booksPrice = 0
  protected cart = new Map<number, number>()
  protected formGroup: FormGroup<CartFormGroup> = this.buildFormGroup()
  protected bookFilter: BookFilterModel = BookFilterModel.createDefaultFilter(CONFIG_NAME)
  protected books: WritableSignal<PageResponseModel<BookTableModel> | null> = signal(null)

  protected readonly paymentMethodEnum = PaymentMethodEnum

  public ngOnInit(): void {
    this.cart = this.cartSessionService.getCart()
    this.cart = new Map(Object.entries(this.cartSessionService.getCart()).map(([key, value]) => [Number(key), value]))

    this.filterBooks()
    this.recalculatePrices()
  }

  removeBookFromCart(id: number) {
    this.cartSessionService.removeBookFromCart(id)
    this.cart.delete(id)
    this.books()!.content = this.books()!.content.filter(book => book.id !== id)
  }

  goToBookDetail(id: number) {
    this.router.navigate([`/book-list/${id}`])
  }

  onSubmit() {
    if (this.formGroup.invalid) {
      this.translateService.get('INVALID_DATA').subscribe((res: string) => {
        this.notificationService.errorNotification(res)
      })
      return
    }

    if (this.cart.size === 0) {
      this.translateService.get('CART_CANNOT_BE_EMPTY').subscribe((res: string) => {
        this.notificationService.errorNotification(res)
      })
      return
    }

    this.createCartCreateModel()
  }

  recalculatePrices() {
    this.booksPrice = Array.from(this.cart.entries()).reduce((acc, [bookId, quantity]) => {
      const book = this.books()!.content.find(model => model.id === bookId)
      return acc + (book ? book.price * quantity : 0)
    }, 0)

    if (!this.formGroup.value.paymentMethod) {
      this.booksTotalPrice = this.booksPrice
    } else {
      this.booksTotalPrice = addPaymentMethodToPrice(this.booksPrice, this.formGroup.value.paymentMethod)
    }
  }

  getEntries() {
    const ids = Array.from(this.cart.entries())
    return ids.map(([bookId, quantity]) => {
      const entry = this.books()?.content.find(book => book.id === bookId)
      return {
        bookId,
        quantity,
        title: entry?.title,
        cost: entry?.price
      }
    })
  }

  private buildFormGroup(): FormGroup {
    const group: CartForm = {
      email: [null, [Validators.required, Validators.email]],
      paymentMethod: [null, [Validators.required]]
    }

    return this.formBuilder.group(group)
  }

  private filterBooks() {
    this.bookFilter.size = this.cart.size
    this.bookFilter.id = new FilterCriteriaModel(FilterOperatorEnum.IN, Array.from(this.cart.keys()))
    if (this.cart.size === 0) {
      this.books.set(null)
      return
    }

    this.bookService.filterBooks(this.bookFilter, false).subscribe((response) => {
      this.books.set(response)
    })
  }

  private createCartCreateModel() {
    const cartItems = Array.from(this.cart.entries()).map(([bookId, quantity]) => new CartItemCreateModel(bookId, quantity))

    this.cartService.createCart(new CartCreateModel(this.formGroup.value.paymentMethod!, cartItems)).subscribe({
      next: () => {
        this.translateService.get('PAYMENT_SUCCESS').subscribe((res: string) => {
          this.notificationService.successNotification(res)
        })
        this.cartSessionService.clearCart()
        this.router.navigate(['/'])
      },
      error: () => {
        this.translateService.get('PAYMENT_ERROR').subscribe((res: string) => {
          this.notificationService.errorNotification(res)
        })
      }
    })
  }
}

