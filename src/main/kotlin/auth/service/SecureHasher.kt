package dev.uliana.auth.service

interface SecureHasher {
    fun hash(value: String): String

    fun verify(value: String, hash: String): Boolean
}
