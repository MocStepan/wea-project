package cz.tul.backend.auth.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class RefreshToken(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  @ManyToOne(optional = false)
  val authUser: AuthUser,
  val createdDateTime: LocalDateTime = LocalDateTime.now()
) {
  companion object {
    fun from(authUser: AuthUser): RefreshToken {
      return RefreshToken(
        authUser = authUser
      )
    }
  }
}
