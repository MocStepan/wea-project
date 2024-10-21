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

  // Input property to receive the list of column definitions from the parent component.
  // The columns define how the filters should be applied.
  @Input({required: true}) columns: ColumnDefModel[] = []

  // Output event emitter to notify the parent component when the filters are applied.
  // It emits an array of updated column definitions.
  @Output() filter = new EventEmitter<ColumnDefModel[]>()

  // Method to apply filters by emitting the current columns (with their filter criteria) to the parent component.
  applyFilters() {
    this.filter.emit(this.columns)
  }

  // Type guard method to check if a column type is an EnumColumnTypeModel.
  // This is useful when filtering specific types like enums.
  isEnumColumnType(type: ColumnType): type is EnumColumnTypeModel {
    return type instanceof EnumColumnTypeModel
  }

  // Method to clear all filter criteria from the columns by setting each filter value to null.
  // After clearing, the updated columns are applied via applyFilters().
  clearFilters() {
    this.columns.forEach(column => {
      column.filterCriteria.value = null
    })
    this.applyFilters()
  }
}
