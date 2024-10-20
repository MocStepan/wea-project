import {FilterCriteriaModel} from '../../../shared/filter/model/filter-criteria.model'

export class BookFilterModel {
  size: number
  page: number
  categories: FilterCriteriaModel | null

  constructor(size: number, page: number, categories: FilterCriteriaModel | null = null) {
    this.size = size
    this.page = page
    this.categories = categories
  }

  static createDefaultFilter(): BookFilterModel {
    return new BookFilterModel(9, 0)
  }

}
