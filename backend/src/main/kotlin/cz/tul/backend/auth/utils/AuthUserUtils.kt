package cz.tul.backend.auth.utils

import cz.tul.backend.auth.entity.AuthUser

fun AuthUser.getAuthUserFullName(): String {
  return this.firstName + " " + this.lastName
}
