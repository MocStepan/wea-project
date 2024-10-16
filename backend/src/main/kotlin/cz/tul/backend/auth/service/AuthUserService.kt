package cz.tul.backend.auth.service

import cz.tul.backend.auth.base.dto.AuthJwtClaims
import cz.tul.backend.auth.dto.AuthUserDTO
import cz.tul.backend.auth.repository.AuthUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class AuthUserService(
  private val authUserRepository: AuthUserRepository
) {

  fun getAuthUser(claims: AuthJwtClaims): AuthUserDTO? {
    val authUser = authUserRepository.findByIdOrNull(claims.authUserId)
    if (authUser == null) {
      log.warn { "AuthUser with id ${claims.authUserId} not found" }
      return null
    }

    return AuthUserDTO.from(authUser)
  }
}
