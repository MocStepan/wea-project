package cz.tul.backend.auth.repository

import cz.tul.backend.auth.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
  fun findByAuthUser_Id(id: Long): Set<RefreshToken>
}
