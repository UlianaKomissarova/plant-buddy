package dev.uliana.auth.service

import org.apache.commons.codec.digest.DigestUtils

object TokenHasher : dev.uliana.auth.service.SecureHasher {
    override fun hash(value: String): String {
        return DigestUtils.sha256Hex(value)
    }

    override fun verify(value: String, hash: String): Boolean {
        return hash(value) == hash
    }
}
