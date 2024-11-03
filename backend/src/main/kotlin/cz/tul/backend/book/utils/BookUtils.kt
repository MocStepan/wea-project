package cz.tul.backend.book.utils

/**
 * Special method that can be used by string to get list of categories from string separated by comma.
 *
 * @return list of categories
 */
fun String.splitCategories() = this.split(",").map { it.trim() }

/**
 * Special method that can be used by string to get list of authors from string separated by semicolon.
 *
 * @return list of authors
 */
fun String.splitAuthors() = this.split(";").map { it.trim() }
