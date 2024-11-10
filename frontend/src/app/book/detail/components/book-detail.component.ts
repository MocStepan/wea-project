import {CommonModule, NgOptimizedImage} from '@angular/common'
import {Component, EventEmitter, inject, Input, OnInit, Output, signal, WritableSignal} from '@angular/core'
import {FormsModule, ReactiveFormsModule} from '@angular/forms'
import {MatCard} from '@angular/material/card'
import {MatError, MatFormField, MatLabel} from '@angular/material/form-field'
import {MatInput} from '@angular/material/input'
import {ActivatedRoute} from '@angular/router'
import {TranslateModule, TranslateService} from '@ngx-translate/core'
import moment from 'moment'
import { RatingModule } from 'primeng/rating';
import {AuthService} from '../../../auth/service/auth.service'
import {NotificationService} from '../../../shared/notification/notification.service'
import {BookService} from '../../service/book.service'
import {BookCommentCreateModel} from '../model/book-comment-create.model'
import { BookRatingCreateModel } from '../model/book-rating-create.model';
import {BookDetailModel} from '../model/book-detail.model'
import {MatIcon} from '@angular/material/icon'
import {MatTooltip} from '@angular/material/tooltip'
import {MatIconButton} from '@angular/material/button'
import {error} from '@angular/compiler-cli/src/transformers/util'

/**
 * Component for the book detail.
 */
@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, MatLabel, MatFormField, MatCard, NgOptimizedImage, TranslateModule, MatInput, RatingModule, MatIcon, MatTooltip, MatIconButton, MatError],
  providers: [],
  templateUrl: './book-detail.component.html',
  styleUrls: ['../../style/book.component.css']
})
export class BookDetailComponent implements OnInit  {
  book: WritableSignal<BookDetailModel | null> = signal(null)
  commentInput: WritableSignal<string> = signal('')
  ratingInput: WritableSignal<number> = signal(0)
  private bookId: number | null = null
  protected readonly moment = moment

  private ratingExists = false
  private bookService: BookService = inject(BookService)
  private authService: AuthService = inject(AuthService)
  private route = inject(ActivatedRoute)
  private translate: TranslateService = inject(TranslateService)
  private notificationService = inject(NotificationService)
  ratingArr: number[] = [];

  /**
   * Initializes the component. Fetches the book detail based on the book id.
   */
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const bookId = params['id']
      if (bookId) {
        this.bookId = bookId
        this.getBookDetail()
        this.onBookGetRating()
      }
    })
    for (let index = 0; index < 5; index++) {
      this.ratingArr.push(index);
    }
  }

  /**
   * Handles the click event for the rating.
   * @param rating
   */
  onBookOnClick(rating: number) {
    this.ratingInput.set(rating)
    return false;
  }

  /**
   * Shows the icon based on the rating.
   * @param index
   */
  onBookShowIcon(index: number) {
    if (this.ratingInput() >= index + 1) {
      return 'star';
    } else {
      return 'star_border';
    }
  }

  /**
   * Fetches the rating for the book.
   * Sets the rating input and updates the rating existence flag.
   *
   * @private
   */
  private onBookGetRating(): void {
    this.bookService.getRating(this.bookId!).subscribe((response) => {
      this.ratingInput.set(response.rating ?? 0)
      this.ratingExists = response.rating !== null
    })
  }

  /**
   * Fetches the book detail based on the book id.
   *
   * @see BookService.getBookDetail
   * @see BookDetailModel
   * @private
   */
  private getBookDetail(): void {
    this.bookService.getBookDetail(this.bookId!).subscribe({
      next: (response) => {
        this.book.set(response)
      },
      error: () => {
        this.translate.get('auth.Error').subscribe((res: string) => {
          this.notificationService.errorNotification(res)
        })
      }
    })
  }

  /**
   * Checks if the user is signed in.
   *
   * @returns {boolean} True if the user is signed in, false otherwise.
   */
  isSignedIn() {
    return this.authService.isSignedIn()
  }


  /**
   * Submits a comment for the book.
   */
  onBookSubmitComment() {
    this.bookService.createComment(this.bookId!, new BookCommentCreateModel(this.commentInput())).subscribe({
      next: () => {
        this.translate.get('COMMENT_POSTED').subscribe((res: string) => {
          this.notificationService.errorNotification(res)
        })
        this.getBookDetail()
      },
      error: () => {
        this.translate.get('POST_COMMENT_ERROR').subscribe((res: string) => {
          this.notificationService.errorNotification(res)
        })
      }
    })
  }

  /**
   * Deletes the rating for the book.
   */
  onBookDeleteRating() {
    this.bookService.deleteRating(this.bookId!).subscribe({
      next: () => {
        this.translate.get('RATING_DELETED').subscribe((res: string) => {
          this.notificationService.errorNotification(res)
        })
        this.ratingInput.set(0)
        this.ratingExists = false
        this.getBookDetail()
      },
      error: () => {
        this.translate.get('RATING_DELETED_ERROR').subscribe((res: string) => {
          this.notificationService.errorNotification(res)
        })
      }
    })
  }

  /**
   * Submits a rating for the book.
   * If the rating does not exist, it creates a new rating.
   * If the rating exists, it updates the existing rating.
   */
  onBookSubmitRating() {
    if (!this.ratingExists) {
      this.bookService
        .createRating(this.bookId!, new BookRatingCreateModel(this.ratingInput()))
        .subscribe({
          next: () => {
            this.translate.get('RATING_POSTED').subscribe((res: string) => {
              this.notificationService.errorNotification(res)
            })
            this.ratingExists = true;
            this.getBookDetail();
          },
          error: () => {
            this.translate.get('RATING_POSTED_ERROR').subscribe((res: string) => {
              this.notificationService.errorNotification(res)
            })
            this.notificationService.errorNotification('Failed to post rating');
          },
        });
    } else {
      this.bookService
        .updateRating(this.bookId!, new BookRatingCreateModel(this.ratingInput()))
        .subscribe({
          next: () => {
            this.translate.get('RATING_POSTED').subscribe((res: string) => {
              this.notificationService.errorNotification(res)
            })
            this.notificationService.successNotification('Rating posted');
            this.getBookDetail();
          },
          error: () => {
            this.translate.get('RATING_POSTED_ERROR').subscribe((res: string) => {
              this.notificationService.errorNotification(res)
            })
            this.notificationService.errorNotification('Failed to post rating');
          },
        });
    }
  }
}
