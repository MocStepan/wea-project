package cz.tul.backend.common.filter.dto

data class PageResponseDTO<T>(
  val content: List<T>,
  val page: Int,
  val size: Int,
  val totalPages: Int,
  val isEmpty: Boolean
)
