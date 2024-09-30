package cz.tul.backend.auth.repository

import cz.tul.backend.auth.entity.AuthUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthUserRepository : JpaRepository<AuthUser, Long> {
  fun findByEmail(email: String): AuthUser?

  fun existsByEmail(email: String): Boolean
}
