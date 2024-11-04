import {BookAuthorDetailModel} from './book-author-detail.model'
import {BookCategoryDetailModel} from './book-category-detail.model'
import {BookCommentDetailModel} from './book-coment-detail.model'

export interface BookDetailModel {
  isbn13: string;
  isbn10: string;
  title: string;
  subtitle: string;
  authors: BookAuthorDetailModel[];
  categories: BookCategoryDetailModel[];
  thumbnail: string;
  description: string;
  publishedYear: number;
  averageRating: number;
  numPages: number;
  ratingsCount: number;
  bookComments: BookCommentDetailModel[];
}
