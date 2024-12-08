package cz.tul.backend.common

fun <E> MutableCollection<E>.addNotNull(element: E?) = element?.let { add(it) }
