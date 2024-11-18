package cz.tul.backend.book.service

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.entity.AuthUser_
import cz.tul.backend.auth.repository.AuthUserRepository
import cz.tul.backend.book.entity.BookFavorite_
import cz.tul.backend.book.entity.Book_
import cz.tul.backend.common.filter.dto.FilterCriteria
import cz.tul.backend.common.filter.dto.FilterCriteriaDTO
import cz.tul.backend.common.filter.valueobject.FilterOperator
import org.springframework.stereotype.Service

@Service
class BookFavoriteAccessService(
  private val authUserRepository: AuthUserRepository
) {

  companion object {
    private val BOOK_FAVORITE_USER_KEYS = listOf(
      Book_.FAVORITES,
      BookFavorite_.AUTH_USER
    )
  }

  fun getFavoriteOrDisabledFilter(favorite: Boolean, claims: AuthJwtClaims?): FilterCriteria<Any> {
    if (!favorite || claims == null || !authUserRepository.existsById(claims.authUserId)) {
      return FilterCriteria.convertAndBuild(FilterCriteriaDTO(FilterOperator.EQUAL, false), Book_.DISABLED)
    }

    return FilterCriteria.convertAndBuild(
      FilterCriteriaDTO(FilterOperator.EQUAL, claims.authUserId),
      AuthUser_.ID,
      BOOK_FAVORITE_USER_KEYS
    )
  }
}
