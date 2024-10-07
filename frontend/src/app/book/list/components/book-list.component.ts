import {Component} from '@angular/core';
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
import {MatButton} from '@angular/material/button';
import {MatGridList, MatGridTile} from '@angular/material/grid-list';
import {NgForOf} from '@angular/common';

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
    NgForOf
  ],
  providers: [],
  templateUrl: './book-list.component.html',
  styleUrls: ['../../style/book.component.css']
})
export class BookListComponent {
  protected cards = Array(10).fill(null);
}
