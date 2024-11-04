import {CommonModule, NgOptimizedImage} from '@angular/common'
import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core'
import {FormsModule, ReactiveFormsModule} from '@angular/forms'
import {MatCard} from '@angular/material/card'
import {MatFormField, MatLabel} from '@angular/material/form-field'
import {MatInput} from '@angular/material/input'
import {ActivatedRoute} from '@angular/router'
import {TranslateModule} from '@ngx-translate/core'
import moment from 'moment'

import {AuthService} from '../../../auth/service/auth.service'
import {NotificationService} from '../../../shared/notification/notification.service'
import {BookService} from '../../service/book.service'
import {BookCommentCreateModel} from '../model/book-comment-create.model'
import {BookDetailModel} from '../model/book-detail.model'

/**
 * Component for the book detail.
 */
@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, MatLabel, MatFormField, MatCard, NgOptimizedImage, TranslateModule, MatInput],
  providers: [],
  templateUrl: './book-detail.component.html',
  styleUrls: ['../../style/book.component.css']
})
export class BookDetailComponent implements OnInit {
  book: WritableSignal<BookDetailModel | null> = signal(null)
  commentInput: WritableSignal<string> = signal('')
  private bookId: number | null = null
  protected readonly moment = moment

  // Injects bookService instead of using constructor injection.
  private bookService: BookService = inject(BookService)
  private authService: AuthService = inject(AuthService)
  private route = inject(ActivatedRoute)
  private notificationService = inject(NotificationService)

  /**
   * Initializes the component. Fetches the book detail based on the book id.
   */
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const bookId = params['id']
      if (bookId) {
        this.bookId = bookId
        this.getBookDetail()
      }
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
        this.notificationService.errorNotification('Failed to fetch book detail')
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
  submitComment() {
    this.bookService.createComment(this.bookId!, new BookCommentCreateModel(this.commentInput())).subscribe({
      next: () => {
        this.notificationService.successNotification('Comment posted')
        this.getBookDetail()
      },
      error: () => {
        this.notificationService.errorNotification('Failed to post comment')
      }
    })
  }
}
