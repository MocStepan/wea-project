package cz.tul.backend.auth.service

import cz.tul.backend.auth.dto.AuthLoginDTO
import cz.tul.backend.auth.dto.AuthRegisterDTO
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.repository.AuthUserRepository
import cz.tul.backend.auth.valueobject.AuthPasswordServiceRegisterError
import cz.tul.backend.shared.serviceresult.ServiceResult
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger { }

@Service
@Transactional
class AuthPasswordService(
  private val authUserRepository: AuthUserRepository,
  private val passwordEncoder: PasswordEncoder,
  private val authCookieService: AuthCookieService
) {
  fun login(
    loginDTO: AuthLoginDTO,
    response: HttpServletResponse
  ): Boolean {
    if (!loginDTO.isValid()) {
      log.warn { "Invalid login request: $loginDTO" }
      return false
    }

    val authUser = authUserRepository.findByEmail(loginDTO.email.value) ?: return false
    if (!passwordEncoder.matches(loginDTO.password, authUser.password)) {
      log.warn { "Invalid password for user: ${loginDTO.email}" }
      return false
    }

    return authCookieService.authenticate(authUser, response)
  }

  fun register(authRegisterDTO: AuthRegisterDTO): ServiceResult<Boolean, AuthPasswordServiceRegisterError> {
    if (!authRegisterDTO.isValid()) {
      log.warn { "Invalid register request: $authRegisterDTO" }
      return ServiceResult.Error(AuthPasswordServiceRegisterError.INVALID_DATA)
    }

    if (authUserRepository.existsByEmail(authRegisterDTO.email.value)) {
      log.warn { "User already exists: ${authRegisterDTO.email}" }
      return ServiceResult.Error(AuthPasswordServiceRegisterError.USER_ALREADY_EXISTS)
    }

    val hashedPassword = passwordEncoder.encode(authRegisterDTO.password)
    val authUser = AuthUser.from(authRegisterDTO, hashedPassword)
    authUserRepository.save(authUser)
    return ServiceResult.Success(true)
  }
}
