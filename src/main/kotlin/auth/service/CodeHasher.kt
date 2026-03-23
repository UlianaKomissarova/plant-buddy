package dev.uliana.auth.service

import org.mindrot.jbcrypt.BCrypt

object CodeHasher : SecureHasher {
    override fun hash(value: String): String {
        return BCrypt.hashpw(value, BCrypt.gensalt(12))
    }

    override fun verify(value: String, hash: String): Boolean {
        if (hash.isBlank()) return false

        return BCrypt.checkpw(value, hash)
    }
}
