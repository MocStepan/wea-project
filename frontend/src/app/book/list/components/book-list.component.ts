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
import {Router} from '@angular/router'
import {TranslateModule} from '@ngx-translate/core'
import {combineLatestWith} from 'rxjs'

import {FilterComponent} from '../../../shared/filter/component/filter.component'
import {ColumnDefModel} from '../../../shared/filter/model/column-def.model'
import {EnumColumnTypeModel} from '../../../shared/filter/model/enum-column-type.model'
import {FilterCriteriaModel} from '../../../shared/filter/model/filter-criteria.model'
import {PageResponseModel} from '../../../shared/filter/model/page-response.model'
import {FilterOperatorEnum} from '../../../shared/filter/valueobject/filter-operator.enum'
import {BookDetailComponent} from '../../detail/components/book-detail.component'
import {BookService} from '../../service/book.service'
import {BookFilterModel} from '../model/book-filter.model'
import {BookTableModel} from '../model/book-table.model'

const CONFIG_NAME = 'book-list'

/**
 * Component for the book list.
 */
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
    TranslateModule,
    FilterComponent,
    BookDetailComponent
  ],
  providers: [],
  templateUrl: './book-list.component.html',
  styleUrls: ['../../style/book.component.css']
})
export class BookListComponent implements OnInit {
  protected books: WritableSignal<PageResponseModel<BookTableModel> | null> = signal(null)
  protected bookFilter: BookFilterModel = BookFilterModel.createDefaultFilter(CONFIG_NAME)
  protected columns: WritableSignal<ColumnDefModel[]> = signal([])
  private router: Router = inject(Router)

  // Injects bookService instead of using constructor injection.
  private bookService: BookService = inject(BookService)

  /**
   * Initializes the component by fetching authors and categories options and setting up the column definitions.
   */
  ngOnInit(): void {
    this.bookService.getBookAuthorsOptionViews().pipe(
      combineLatestWith(this.bookService.getBookCategoriesOptionViews())
    ).subscribe(([authors, categories]) => {
      this.columns.set([
        new ColumnDefModel('SEARCH_ISBN13', 'isbn13', 'string',
          new FilterCriteriaModel(FilterOperatorEnum.ILIKE, this.bookFilter.isbn13?.value)),
        new ColumnDefModel('SEARCH_ISBN10', 'isbn10', 'string',
          new FilterCriteriaModel(FilterOperatorEnum.ILIKE, this.bookFilter.isbn10?.value)),
        new ColumnDefModel('SEARCH_TITLE', 'title', 'string',
          new FilterCriteriaModel(FilterOperatorEnum.ILIKE, this.bookFilter.title?.value)),
        new ColumnDefModel('SEARCH_AUTHORS', 'authors', EnumColumnTypeModel.fromOptionViews(authors),
          new FilterCriteriaModel(FilterOperatorEnum.IN, this.bookFilter.authors?.value)),
        new ColumnDefModel('SEARCH_CATEGORY', 'categories',
          EnumColumnTypeModel.fromOptionViews(categories),
          new FilterCriteriaModel(FilterOperatorEnum.IN, this.bookFilter.categories?.value))
      ])
    })

    this.filterBooks()
  }

  /**
   * Updates the book filter with the selected column definition and filters the books.
   *
   * @param columnDef
   * @see ColumnDefModel
   */
  filterBooksWithColumnDef(columnDef: ColumnDefModel[]) {
    this.bookFilter = ColumnDefModel.prepareColumns(columnDef, this.bookFilter)
    this.filterBooks()
  }

  /**
   * Calls the book service to filter the books with the current book filter.
   *
   * @see BookService
   */
  filterBooks(): void {
    sessionStorage.setItem(CONFIG_NAME, JSON.stringify(this.bookFilter))
    this.bookService.filterBooks(this.bookFilter).subscribe((response) => {
      this.books.set(response)
    })
  }

  /**
   * Changes the page size and index of the book filter and filters the books.
   *
   * @param event is the page event emitted by the paginator.
   */
  onChangePage(event: PageEvent) {
    this.bookFilter.size = event.pageSize
    this.bookFilter.page = event.pageIndex
    this.filterBooks()
  }

  /**
   * Navigates to the book detail page for the given book ID.
   *
   * @param bookId - The ID of the book to view details for.
   */
  goToBookDetail(bookId: number) {
    this.router.navigate([`/book-list/${bookId}`])
  }
}

