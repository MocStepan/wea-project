package cz.tul.backend.auth.base.jwt

interface JwtClaimsCreatorService<T, K> {

  fun createClaims(value: T): K
}
