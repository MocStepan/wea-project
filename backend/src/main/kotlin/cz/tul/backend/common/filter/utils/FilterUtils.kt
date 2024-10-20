package cz.tul.backend.common.filter.utils

fun convertPossibleStringList(value: String?): Any? {
  return if (isList(value)) {
    parseList(value!!)
  } else {
    value
  }
}

private fun parseList(value: String): List<String> {
  return value.substring(1, value.length - 1).split(",")
}

private fun isList(value: String?): Boolean {
  return value != null && value.startsWith('[') && value.endsWith(']')
}
