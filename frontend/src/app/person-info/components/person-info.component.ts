import {CommonModule} from '@angular/common'
import {ChangeDetectionStrategy, Component, inject, OnInit, signal, WritableSignal} from '@angular/core'
import {FormBuilder, FormGroup, FormsModule, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms'
import {MatButton} from '@angular/material/button'
import {MatButtonToggleModule} from '@angular/material/button-toggle'
import {MatCardModule} from '@angular/material/card'
import {MatCheckboxModule} from '@angular/material/checkbox'
import {MatNativeDateModule} from '@angular/material/core'
import {MatDatepickerModule} from '@angular/material/datepicker'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {MatSelectModule} from '@angular/material/select'
import {TranslateModule, TranslateService} from '@ngx-translate/core'

import {BookService} from '../../book/service/book.service'
import {OptionViewModel} from '../../shared/filter/model/option-view.model'
import {NotificationService} from '../../shared/notification/notification.service'
import {PersonInfoForm, PersonInfoFormGroup, PersonInfoFormValue} from '../model/person-info.form'
import {PersonInfoModel} from '../model/person-info.model'
import {
  isPersonAddressEqual,
  PersonInfoAddressForm,
  PersonInfoAddressFormGroup
} from '../model/person-info-address.form'
import {PersonInfoService} from '../service/person-info.service'


@Component({
  selector: 'app-person-info',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    TranslateModule,
    MatFormFieldModule,
    MatCheckboxModule,
    MatButtonToggleModule,
    MatDatepickerModule,
    MatSelectModule,
    MatCardModule,
    MatNativeDateModule,
    MatInputModule,
    MatButton
  ],
  providers: [
    FormBuilder,
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: PersonInfoComponent
    }
  ],
  templateUrl: './person-info.component.html',
  styleUrls: ['./person-info.component.css']
})
export class PersonInfoComponent implements OnInit {
  private translateService = inject(TranslateService)
  private personInfoService = inject(PersonInfoService)
  private bookService = inject(BookService)
  private notificationService = inject(NotificationService)
  private formBuilder: FormBuilder = inject(FormBuilder)

  private created = false

  protected personalAddressFormGroup: FormGroup<PersonInfoAddressFormGroup> = this.buildAddressFormGroup()
  protected billingAddressFormGroup: FormGroup<PersonInfoAddressFormGroup> = this.buildAddressFormGroup()
  protected formGroup: FormGroup<PersonInfoFormGroup> = this.buildFormGroup()
  protected isBillingSameAsPersonal: WritableSignal<boolean> = signal(true)
  protected bookCategories: OptionViewModel[] = []

  /**
   * Initializes the component. If the user info is already created, it will be loaded and displayed.
   */
  ngOnInit(): void {
    this.getPersonInfo()
    this.getBookCategories()
  }

  private getPersonInfo() {
    this.personInfoService.getUserInfo().subscribe({
      next: (response) => {
        const formValue = PersonInfoFormValue(response)
        this.formGroup.patchValue(formValue)
        this.created = true
        this.isBillingSameAsPersonal.set(isPersonAddressEqual(formValue.personalAddress, formValue.billingAddress))
      },
      error: () => {
        this.created = false
        this.isBillingSameAsPersonal.set(false)
      }
    })
  }

  private getBookCategories() {
    this.bookService.getBookCategoriesOptionViews().subscribe((response) => {
      this.bookCategories = response
    })
  }

  /**
   * Checks if the billing address is the same as the personal address.
   * If the billing address is the same as the personal address, the billing address form group will be
   * updated with the personal address form group.
   * @private
   */
  private changeBillingAddress(): void {
    if (this.isBillingSameAsPersonal()) {
      this.billingAddressFormGroup.patchValue(this.personalAddressFormGroup.getRawValue())
    }
  }

  /**
   * Synchronizes the addresses. If the billing address is the same as the personal address, the billing address
   */
  syncAddresses(): void {
    this.isBillingSameAsPersonal.set(!this.isBillingSameAsPersonal())
    this.changeBillingAddress()
  }

  /**
   * Submits the form. If the user info is already created, it will be updated, otherwise it will be created.
   */
  onSubmit(): void {
    this.changeBillingAddress()
    const personInfoModel = PersonInfoModel(this.formGroup.getRawValue())
    if (this.created) {
      this.updatePersonInfo(personInfoModel)
    } else {
      this.createPersonInfo(personInfoModel)
    }
  }

  /**
   * Creates the user info.
   * @param personInfoModel
   * @private
   */
  private createPersonInfo(personInfoModel: PersonInfoModel) {
    this.personInfoService.createUserInfo(personInfoModel).subscribe({
      next: () => {
        this.created = true
        this.translateService.get('INFO_POSTED').subscribe((res: string) => {
          this.notificationService.successNotification(res)
        })
      },
      error: () => {
        this.created = false
        this.translateService.get('INFO_ERROR').subscribe((res: string) => {
          this.notificationService.errorNotification(res)
        })
      }
    })
  }

  /**
   * Updates the user info.
   * @param personInfoModel
   * @private
   */
  private updatePersonInfo(personInfoModel: PersonInfoModel) {
    this.personInfoService.updateUserInfo(personInfoModel).subscribe({
      next: () => {
        this.translateService.get('INFO_POSTED').subscribe((res: string) => {
          this.notificationService.successNotification(res)
        })
      },
      error: () => {
        this.translateService.get('INFO_ERROR').subscribe((res: string) => {
          this.notificationService.errorNotification(res)
        })
      }
    })
  }

  /**
   * Builds the form group.
   * @private
   */
  private buildFormGroup(): FormGroup {
    const group: PersonInfoForm = {
      gender: null,
      birthDate: null,
      referenceSource: null,
      processingConsent: null,
      personalAddress: this.personalAddressFormGroup,
      billingAddress: this.billingAddressFormGroup,
      favoriteCategories: []
    }

    return this.formBuilder.group(group)
  }

  /**
   * Builds the address form group.
   * @private
   */
  private buildAddressFormGroup(): FormGroup {
    const group: PersonInfoAddressForm = {
      country: null,
      city: null,
      street: null,
      houseNumber: null,
      zipCode: null
    }

    return this.formBuilder.group(group)
  }
}
