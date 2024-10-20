import {NgForOf, NgIf, NgOptimizedImage} from '@angular/common'
import {ChangeDetectionStrategy, Component, inject, OnInit, signal, WritableSignal} from '@angular/core'
import {FormsModule} from '@angular/forms'
import {MatOption} from '@angular/material/autocomplete'
import {MatButton, MatIconButton} from '@angular/material/button'
import {
  MatCard,
  MatCardActions,
  MatCardAvatar,
  MatCardContent,
  MatCardHeader,
  MatCardImage,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card'
import {MatFormField, MatLabel} from '@angular/material/form-field'
import {MatGridList, MatGridTile} from '@angular/material/grid-list'
import {MatIcon} from '@angular/material/icon'
import {MatInput} from '@angular/material/input'
import {MatPaginator, PageEvent} from '@angular/material/paginator'
import {MatSelect} from '@angular/material/select'
import {TranslateModule} from '@ngx-translate/core'

import {FilterCriteriaModel} from '../../../shared/filter/model/filter-criteria.model'
import {PageResponseModel} from '../../../shared/filter/model/page-response.model'
import {FilterOperatorEnum} from '../../../shared/filter/valueobject/filter-operator.enum'
import {BookService} from '../../service/book.service'
import {BookFilterModel} from '../model/book-filter.model'
import {BookTableModel} from '../model/book-table.model'

@Component({
  selector: 'app-book-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    MatCardSubtitle,
    MatCardTitle,
    MatCardAvatar,
    MatCardHeader,
    MatCard,
    MatCardContent,
    MatCardImage,
    MatCardActions,
    MatButton,
    MatGridTile,
    MatGridList,
    NgForOf,
    MatPaginator,
    MatFormField,
    MatLabel,
    MatOption,
    MatSelect,
    NgIf,
    NgOptimizedImage,
    MatIcon,
    MatInput,
    MatIconButton,
    FormsModule,
    TranslateModule
  ],
  providers: [],
  templateUrl: './book-list.component.html',
  styleUrls: ['../../style/book.component.css']
})
export class BookListComponent implements OnInit {
  protected books: WritableSignal<PageResponseModel<BookTableModel> | null> = signal(null)
  protected bookFilter: BookFilterModel = BookFilterModel.createDefaultFilter()
  protected value: string | null = null
  private bookService: BookService = inject(BookService)

  ngOnInit(): void {
    this.bookService.filterBooks(this.bookFilter).subscribe((response) => {
      this.books.set(response)
    })
  }

  filterBooks(): void {
    this.bookService.filterBooks(this.bookFilter).subscribe((response) => {
      this.books.set(response)
    })
  }

  onChangePage(event: PageEvent) {
    this.bookFilter.size = event.pageSize
    this.bookFilter.page = event.pageIndex
    this.filterBooks()
  }

  onChangeGenre() {
    if (this.value == '') {
      this.bookFilter.categories = null
    } else {
      this.bookFilter.categories = new FilterCriteriaModel(FilterOperatorEnum.EQUAL, this.value)
    }
    this.filterBooks()
  }
}
