import {animate, state, style, transition, trigger} from '@angular/animations'
import {NgIf} from '@angular/common'
import {ChangeDetectionStrategy, Component, inject, OnInit, signal, WritableSignal} from '@angular/core'
import {MatIconButton} from '@angular/material/button'
import {MatIcon} from '@angular/material/icon'
import {MatPaginator, PageEvent} from '@angular/material/paginator'
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
import {TranslateModule} from '@ngx-translate/core'
import moment from 'moment/moment'

import {PaymentMethodEnum} from '../../cart/valueobject/payment-method.enum'
import {FilterComponent} from '../../shared/filter/component/filter.component'
import {ColumnDefModel} from '../../shared/filter/model/column-def.model'
import {EnumColumnTypeModel} from '../../shared/filter/model/enum-column-type.model'
import {FilterCriteriaModel} from '../../shared/filter/model/filter-criteria.model'
import {PageResponseModel} from '../../shared/filter/model/page-response.model'
import {FilterOperatorEnum} from '../../shared/filter/valueobject/filter-operator.enum'
import {OrderFilterModel} from '../model/order.filter.model'
import {OrderModel} from '../model/order.model'
import {OrderService} from '../service/order.service'

const CONFIG_NAME = 'order-list'

/**
 * Component for the order.
 */
@Component({
  selector: 'app-order',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    FilterComponent,
    NgIf,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    TranslateModule,
    MatIconButton,
    MatIcon,
    MatHeaderRowDef,
    MatRowDef,
    MatHeaderRow,
    MatRow,
    MatPaginator,
    MatCellDef,
    MatHeaderCellDef
  ],
  providers: [],
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ])
  ],
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css']
})
export class OrderComponent implements OnInit {
  private orderService = inject(OrderService)
  protected orderFilter = OrderFilterModel.createDefaultFilter(CONFIG_NAME)
  protected orders: WritableSignal<PageResponseModel<OrderModel> | null> = signal(null)
  protected columns: WritableSignal<ColumnDefModel[]> = signal([])
  protected expandedElement: OrderModel | null = null

  protected readonly moment = moment
  protected readonly orderItemColumnsToDisplay = ['bookName', 'quantity', 'price']
  protected readonly orderColumnsToDisplayWithExpand = ['id', 'paymentMethod', 'totalPrice', 'createdDateTime', 'expand']

  ngOnInit(): void {
    this.columns.set([
      new ColumnDefModel('SEARCH_PAYMENT_METHOD', 'paymentMethod',
        EnumColumnTypeModel.fromEnumValue(Object.keys(PaymentMethodEnum), false),
        new FilterCriteriaModel(FilterOperatorEnum.EQUAL, this.orderFilter.paymentMethod?.value)),
      new ColumnDefModel('SEARCH_TOTAL_PRICE', 'totalPrice', 'string',
        new FilterCriteriaModel(FilterOperatorEnum.EQUAL, this.orderFilter.totalPrice?.value))
    ])

    this.filterOrders()
  }

  private filterOrders(): void {
    sessionStorage.setItem(CONFIG_NAME, JSON.stringify(this.orderFilter))
    this.orderService.filterOrders(this.orderFilter).subscribe((response) => {
      this.orders.set(response)
    })
  }

  onChangePage(event: PageEvent) {
    this.orderFilter.size = event.pageSize
    this.orderFilter.page = event.pageIndex
    this.filterOrders()
  }

  filterBooksWithColumnDef(columnDef: ColumnDefModel[]) {
    this.orderFilter = ColumnDefModel.prepareColumns(columnDef, this.orderFilter)
    this.filterOrders()
  }
}

