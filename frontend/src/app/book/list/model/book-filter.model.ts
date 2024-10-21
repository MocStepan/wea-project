import {FilterCriteriaModel} from '../../../shared/filter/model/filter-criteria.model'

export class BookFilterModel {
  size: number
  page: number
  isbn13: FilterCriteriaModel | null
  isbn10: FilterCriteriaModel | null
  title: FilterCriteriaModel | null
  authors: FilterCriteriaModel | null
  categories: FilterCriteriaModel | null

  [prop: string]: number | FilterCriteriaModel | null

  constructor(size: number, page: number, categories: FilterCriteriaModel | null = null,
              isbn13: FilterCriteriaModel | null = null, isbn10: FilterCriteriaModel | null = null,
              title: FilterCriteriaModel | null = null, authors: FilterCriteriaModel | null = null
  ) {
    this.size = size
    this.page = page
    this.isbn13 = isbn13
    this.isbn10 = isbn10
    this.title = title
    this.authors = authors
    this.categories = categories
  }

  static createDefaultFilter(): BookFilterModel {
    return new BookFilterModel(9, 0)
  }

}
