import {NgForOf, NgIf} from '@angular/common'
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  inject,
  Input,
  signal,
  WritableSignal
} from '@angular/core'
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatOption} from '@angular/material/autocomplete'
import {MatButton} from '@angular/material/button'
import {MatCard, MatCardActions, MatCardContent, MatCardTitle} from '@angular/material/card'
import {MatFormField, MatLabel} from '@angular/material/form-field'
import {MatInput} from '@angular/material/input'
import {MatList, MatListItem} from '@angular/material/list'
import {MatSelect} from '@angular/material/select'
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from '@angular/material/table'
import {Router} from '@angular/router'
import {TranslateModule, TranslateService} from '@ngx-translate/core'

import {BookTableModel} from '../../book/list/model/book-table.model'
import {PageResponseModel} from '../../shared/filter/model/page-response.model'
import {NotificationService} from '../../shared/notification/notification.service'
import {CartForm, CartFormGroup} from '../model/cart.form'
import {CartCreateModel} from '../model/cart-create.model'
import {CartItemCreateModel} from '../model/cart-item-create.model'
import {CartSessionItem, CartSessionItemView} from '../model/cart-session-item.model'
import {CartService} from '../service/cart.service'
import {CartSessionService} from '../service/cart-session.service'
import {addPaymentMethodToPrice, PaymentMethodEnum} from '../valueobject/payment-method.enum'

/**
 * Component for the cart submit step.
 */
@Component({
  selector: 'app-cart-confirmation-step',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    ReactiveFormsModule,
    TranslateModule,
    MatCardTitle,
    MatCard,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    MatCardActions,
    MatButton,
    MatCardContent,
    MatList,
    MatListItem,
    MatInput,
    NgIf,
    NgForOf,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCellDef,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatHeaderRowDef
  ],
  providers: [FormBuilder],
  templateUrl: './cart-confirmation-step.component.html',
  styleUrls: ['../style/cart.component.css']
})
export class CartConfirmationStepComponent {
  @Input({required: true}) public books: WritableSignal<PageResponseModel<BookTableModel> | null> = signal(null)
  @Input({required: true}) public cartSessionItems: CartSessionItem[] = []

  private cartService = inject(CartService)
  private cartSessionService = inject(CartSessionService)
  private translateService = inject(TranslateService)
  private notificationService = inject(NotificationService)
  private formBuilder: FormBuilder = inject(FormBuilder)
  private router = inject(Router)

  protected booksTotalPrice = 0
  protected booksPrice = 0
  protected formGroup: FormGroup<CartFormGroup> = this.buildFormGroup()

  protected readonly cartTableDisplayedColumns = ['title', 'price', 'quantity', 'totalPrice']
  protected readonly paymentMethodEnum = PaymentMethodEnum

  protected cartSessionItemViews = computed(() => {
    if (this.books() !== null) {
      return this.getSessionItemViews()
    }
    return []
  })

  private bookTableEffect = effect(() => {
    if (this.books() !== null) {
      this.recalculatePrices()
    }
  })

  private getSessionItemViews(): CartSessionItemView[] {
    return this.cartSessionItems.map(item => {
      const book = this.books()?.content.find(model => model?.id === item.bookId)
      return {
        ...item,
        title: book?.title,
        price: book?.price
      }
    })
  }

  /**
   * Check if the form is valid and calls the service to create the cart.
   * @protected
   */
  protected onSubmit() {
    if (this.formGroup.invalid) {
      this.translateService.get('INVALID_DATA').subscribe((res: string) => {
        this.notificationService.errorNotification(res)
      })
      return
    }

    if (this.cartSessionItems.length === 0) {
      this.translateService.get('CART_CANNOT_BE_EMPTY').subscribe((res: string) => {
        this.notificationService.errorNotification(res)
      })
      return
    }

    const cartCreateModel = new CartCreateModel(
      this.formGroup.value.paymentMethod!,
      CartItemCreateModel(this.cartSessionItems)
    )

    this.cartService.createCart(cartCreateModel).subscribe({
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

  /**
   * Recalculate book's prices and total price with the selected payment method.
   * @protected
   */
  protected recalculatePrices() {
    this.booksPrice = this.cartSessionItems.reduce((acc, item) => {
      const book = this.books()?.content.find(model => model?.id === item.bookId)
      return acc + (book ? book.price * item.quantity : 0)
    }, 0)

    if (!this.formGroup.value.paymentMethod) {
      this.booksTotalPrice = this.booksPrice
    } else {
      this.booksTotalPrice = addPaymentMethodToPrice(this.booksPrice, this.formGroup.value.paymentMethod)
    }
  }

  /**
   * Build the form group.
   *
   * @returns FormGroup
   */
  private buildFormGroup(): FormGroup {
    const group: CartForm = {
      email: [null, [Validators.required, Validators.email]],
      paymentMethod: [null, [Validators.required]]
    }

    return this.formBuilder.group(group)
  }
}

