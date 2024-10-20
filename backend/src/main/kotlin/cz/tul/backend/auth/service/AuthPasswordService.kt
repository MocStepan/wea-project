package cz.tul.backend.auth.service

import cz.tul.backend.auth.dto.AuthLoginDTO
import cz.tul.backend.auth.dto.AuthRegisterDTO
import cz.tul.backend.auth.entity.AuthUser
import cz.tul.backend.auth.repository.AuthUserRepository
import cz.tul.backend.auth.valueobject.AuthPasswordServiceRegisterError
import cz.tul.backend.common.serviceresult.ServiceResult
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger { }

/**
 * Service for handling login and registration of users with password authentication.
 */
@Service
@Transactional
class AuthPasswordService(
  private val authUserRepository: AuthUserRepository,
  private val authPasswordEncoder: AuthPasswordEncoder,
  private val authCookieService: AuthCookieService
) {

  /**
   * Try to log in with the given login data. If successful, set the access cookie to the response.
   *
   * @param loginDTO the login data
   * @param response the response to set the cookie to
   * @return true if the login was successful, false otherwise
   */
  fun login(loginDTO: AuthLoginDTO, response: HttpServletResponse): Boolean {
    if (!loginDTO.isValid()) {
      log.warn { "Invalid login request: $loginDTO" }
      return false
    }

    val authUser = authUserRepository.findByEmail(loginDTO.email.value)
    if (authUser == null) {
      log.warn { "User: ${loginDTO.email} not found" }
      return false
    }

    if (!authPasswordEncoder.matches(loginDTO.password, authUser.password)) {
      log.warn { "Invalid password for user: ${loginDTO.email}" }
      return false
    }

    authCookieService.setAccessCookie(authUser, response, loginDTO.rememberMe)
    return true
  }

  /**
   * Register a new user with the given data, if the data is valid and the user does not already exist.
   *
   * @param authRegisterDTO the register data
   * @return true if the registration was successful, false otherwise
   */
  fun register(authRegisterDTO: AuthRegisterDTO): ServiceResult<Boolean, AuthPasswordServiceRegisterError> {
    if (!authRegisterDTO.isValid()) {
      log.warn { "Invalid register request: $authRegisterDTO" }
      return ServiceResult.Error(AuthPasswordServiceRegisterError.INVALID_DATA)
    }

    if (authUserRepository.existsByEmail(authRegisterDTO.email.value)) {
      log.warn { "User already exists: ${authRegisterDTO.email}" }
      return ServiceResult.Error(AuthPasswordServiceRegisterError.USER_ALREADY_EXISTS)
    }

    val hashedPassword = authPasswordEncoder.encode(authRegisterDTO.password)
    val authUser = AuthUser.from(authRegisterDTO, hashedPassword)
    authUserRepository.save(authUser)
    return ServiceResult.Success(true)
  }
}
