package cz.tul.backend.common.filter.dto

import com.fasterxml.jackson.databind.ObjectMapper
import cz.tul.backend.common.filter.utils.convertValue
import cz.tul.backend.common.filter.valueobject.FilterOperator
import cz.tul.backend.common.filter.valueobject.FilterSort
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

/**
 * Represents a filter criteria.
 *
 * @param T type of the value
 * @property operator operator of the filter
 * @property value value of the filter
 * @property sort sort of the filter
 * @property key key of the filter
 * @property joinsKeys keys of the joins
 */
data class FilterCriteria<out T>(
  val operator: FilterOperator? = null,
  val value: T? = null,
  val sort: FilterSort? = null,
  val key: String,
  val joinsKeys: List<String> = listOf()
) {

  companion object {

    /**
     * Converts and builds a filter criteria.
     * Tries to convert the [FilterCriteriaDTO.value] to a list of strings, else it will be used as is.
     *
     * @param filterCriteriaDTO filter criteria DTO
     * @param key key of the filter
     * @param joinsKeys keys of the joins
     * @return filter criteria
     */
    fun convertAndBuild(
      filterCriteriaDTO: FilterCriteriaDTO,
      key: String,
      joinsKeys: List<String> = listOf()
    ): FilterCriteria<Any> {
      return FilterCriteria(
        operator = filterCriteriaDTO.operator,
        value = filterCriteriaDTO.value,
        sort = filterCriteriaDTO.sort,
        key = key,
        joinsKeys = joinsKeys
      )
    }

    fun <T> convertAndBuild(
      filterCriteriaDTO: FilterCriteriaDTO,
      key: String,
      objectMapper: ObjectMapper,
      clazz: Class<T>,
      joinsKeys: List<String> = listOf()
    ): FilterCriteria<Any>? {
      return try {
        FilterCriteria(
          operator = filterCriteriaDTO.operator,
          value = convertValue(filterCriteriaDTO.value, objectMapper, clazz),
          sort = filterCriteriaDTO.sort,
          key = key,
          joinsKeys = joinsKeys
        )
      } catch (e: Exception) {
        log.info { "Exception creating filterCriteria $key" }
        log.debug(e) { "Exception creating filterCriteria from $filterCriteriaDTO" }
        null
      }
    }

    /**
     * Builds a filter criteria just with the sort and without the value.
     *
     * @param key key of the filter
     * @param filterSort sort of the filter
     * @return filter criteria
     */
    fun buildOrderCriteria(key: String, filterSort: FilterSort): FilterCriteria<Any> {
      return FilterCriteria(
        key = key,
        sort = filterSort
      )
    }
  }
}
