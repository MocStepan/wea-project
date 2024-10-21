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
import {combineLatestWith} from 'rxjs'

import {FilterComponent} from '../../../shared/filter/component/filter.component'
import {ColumnDefModel} from '../../../shared/filter/model/column-def.model'
import {EnumColumnTypeModel} from '../../../shared/filter/model/enum-column-type.model'
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
    TranslateModule,
    FilterComponent
  ],
  providers: [],
  templateUrl: './book-list.component.html',
  styleUrls: ['../../style/book.component.css']
})
export class BookListComponent implements OnInit {

  // Signal to store the list of books, or null if no books are available. This is a reactive signal.
  protected books: WritableSignal<PageResponseModel<BookTableModel> | null> = signal(null)

  // Default filter object used for searching and filtering books.
  protected bookFilter: BookFilterModel = BookFilterModel.createDefaultFilter()

  // Signal for column definitions, defining how data will be presented and filtered in the table.
  protected columns: WritableSignal<ColumnDefModel[]> = signal([])

  // Injecting the BookService to interact with book-related data and API calls.
  private bookService: BookService = inject(BookService)

  // Lifecycle hook called when the component is initialized.
  ngOnInit(): void {
    // Fetches authors and categories options in parallel and sets up the column definitions.
    this.bookService.getBookAuthorsOptionViews().pipe(
      combineLatestWith(this.bookService.getBookCategoriesOptionViews())
    ).subscribe(([authors, categories]) => {
      // Defines the columns for the table including filters for ISBN, title, authors, and categories.
      this.columns.set([
        new ColumnDefModel('SEARCH_ISBN13', 'isbn13', 'string', new FilterCriteriaModel(FilterOperatorEnum.ILIKE)),
        new ColumnDefModel('SEARCH_ISBN10', 'isbn10', 'string', new FilterCriteriaModel(FilterOperatorEnum.ILIKE)),
        new ColumnDefModel('SEARCH_TITLE', 'title', 'string', new FilterCriteriaModel(FilterOperatorEnum.ILIKE)),
        new ColumnDefModel('SEARCH_AUTHORS', 'authors',
          EnumColumnTypeModel.fromOptionViews(authors), new FilterCriteriaModel(FilterOperatorEnum.IN)),
        new ColumnDefModel('SEARCH_CATEGORY', 'categories',
          EnumColumnTypeModel.fromOptionViews(categories), new FilterCriteriaModel(FilterOperatorEnum.IN))
      ])
    })

    // Initiates the initial book filtering process based on the default filter.
    this.filterBooks()
  }

  // Method to filter books based on the column definitions provided. It updates the book filter object.
  filterBooksWithColumnDef(columnDef: ColumnDefModel[]) {
    // Prepares the book filter using the updated column definitions and filters the books.
    this.bookFilter = ColumnDefModel.prepareColumns(columnDef, this.bookFilter)
    this.filterBooks()
  }

  // Method that performs the actual filtering of books by making a call to the BookService.
  filterBooks(): void {
    this.bookService.filterBooks(this.bookFilter).subscribe((response) => {
      // Sets the books response in the reactive signal.
      this.books.set(response)
    })
  }

  // Method that handles pagination events such as changing the page number or page size.
  onChangePage(event: PageEvent) {
    // Updates the book filter with the new page size and index, then filters the books.
    this.bookFilter.size = event.pageSize
    this.bookFilter.page = event.pageIndex
    this.filterBooks()
  }
}

