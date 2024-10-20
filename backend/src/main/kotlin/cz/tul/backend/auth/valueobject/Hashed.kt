package cz.tul.backend.auth.valueobject

/**
 * Custom value object for hashed passwords.
 *
 * @param value the hashed password
 */
@JvmInline
value class Hashed(val value: String)
