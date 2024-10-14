import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {
  MatCard,
  MatCardActions,
  MatCardAvatar,
  MatCardContent,
  MatCardHeader,
  MatCardImage,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';
import {MatButton, MatButtonModule} from '@angular/material/button';
import {MatGridList, MatGridTile} from '@angular/material/grid-list';
import {NgForOf, NgIf, NgOptimizedImage} from '@angular/common';
import {BookService} from '../../service/book.service';
import {PageResponseModel} from '../../../shared/filter/model/page-response.model';
import {BookTableModel} from '../model/book-table.model';
import {BookFilterModel} from '../model/book-filter.model';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatFormField, MatFormFieldModule, MatLabel} from '@angular/material/form-field';
import {MatOption} from '@angular/material/autocomplete';
import {MatSelect} from '@angular/material/select';
import {MatIconModule} from '@angular/material/icon';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {FilterCriteriaModel} from '../../../shared/filter/model/filter-criteria.model';
import {FilterOperatorEnum} from '../../../shared/filter/valueobject/filter-operator.enum';

@Component({
  selector: 'book-list',
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
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    NgIf,
    NgOptimizedImage
  ],
  providers: [],
  templateUrl: './book-list.component.html',
  styleUrls: ['../../style/book.component.css']
})
export class BookListComponent implements OnInit {
  protected books: WritableSignal<PageResponseModel<BookTableModel> | null> = signal(null);
  protected bookFilter: BookFilterModel = BookFilterModel.createDefaultFilter();
  protected value: string | null = null;
  private bookService: BookService = inject(BookService);

  ngOnInit(): void {
    this.bookService.filterBooks(this.bookFilter).subscribe((response) => {
      this.books.set(response);
    });
  }

  filterBooks(): void {
    this.bookService.filterBooks(this.bookFilter).subscribe((response) => {
      this.books.set(response);
    });
  }

  onChangePage(event: PageEvent) {
    this.bookFilter.size = event.pageSize;
    this.bookFilter.page = event.pageIndex;
    this.filterBooks();
  }

  onChangeGenre() {
    if (this.value == '') {
      this.bookFilter.categories = null;
    } else {
      this.bookFilter.categories = new FilterCriteriaModel(FilterOperatorEnum.EQUAL, this.value);
    }
    this.filterBooks();
  }
}
