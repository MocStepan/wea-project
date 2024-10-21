import {NgForOf, NgIf} from '@angular/common'
import {ChangeDetectionStrategy, Component, CUSTOM_ELEMENTS_SCHEMA, EventEmitter, Input, Output} from '@angular/core'
import {FormsModule} from '@angular/forms'
import {MatButton} from '@angular/material/button'
import {MatCheckbox} from '@angular/material/checkbox'
import {MatFormField} from '@angular/material/form-field'
import {MatInput} from '@angular/material/input'
import {MatOption, MatSelect} from '@angular/material/select'
import {TranslateModule} from '@ngx-translate/core'

import {ColumnDefModel, ColumnType} from '../model/column-def.model'
import {EnumColumnTypeModel} from '../model/enum-column-type.model'

@Component({
  selector: 'app-filter',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    FormsModule,
    MatFormField,
    MatSelect,
    MatInput,
    MatOption,
    MatButton,
    MatCheckbox,
    TranslateModule
  ],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.css']
})
export class FilterComponent {
  @Input({required: true}) columns: ColumnDefModel[] = []
  @Output() filter = new EventEmitter<ColumnDefModel[]>

  applyFilters() {
    this.filter.emit(this.columns)
  }

  isEnumColumnType(type: ColumnType): type is EnumColumnTypeModel {
    return type instanceof EnumColumnTypeModel
  }

  clearFilters() {
    this.columns.forEach(column => {
      column.filterCriteria.value = null
    })
    this.applyFilters()
  }
}
