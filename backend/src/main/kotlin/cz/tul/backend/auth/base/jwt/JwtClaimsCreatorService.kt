package cz.tul.backend.auth.base.jwt

/**
 * Service for creating JWT claims.
 */
interface JwtClaimsCreatorService<T, K> {

  /**
   * Creates claims with the provided value.
   *
   * @param value value to be stored in the claims
   * @return created claims
   */
  fun createClaims(value: T): K
}
