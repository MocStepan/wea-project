package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.api.AuthJwtClaims
import cz.tul.backend.auth.dto.AuthUserDTO
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.repository.AuthUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Service for handling AuthUser entities.
 */
@Service
@Transactional
class AuthUserService(
  private val authUserRepository: AuthUserRepository
) {

  /**
   * Gets the authenticated user from the database.
   *
   * @param claims Claims of the authenticated user.
   * @return [AuthUserDTO] of the authenticated user or null if the user was not found.
   */
  fun getAuthUser(claims: AuthJwtClaims): AuthUserDTO? {
    val authUser = authUserRepository.findByIdOrNull(claims.authUserId)
    if (authUser == null) {
      log.warn { "AuthUser with id ${claims.authUserId} not found" }
      return null
    }

    return AuthUserDTO.from(authUser)
  }

  fun getReferenceIfExists(authUserId: Long): AuthUser? = authUserRepository.findByIdOrNull(authUserId)
}
