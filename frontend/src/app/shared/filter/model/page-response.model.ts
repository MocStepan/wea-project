
export interface PageResponseModel<T> {
  content: Array<T>,
  totalPages: number,
  page: number,
  size: number,
  isEmpty: boolean
}
