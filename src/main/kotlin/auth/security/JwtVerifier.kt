package dev.uliana.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import dev.uliana.config.AppConfig

object JwtVerifier {
    private val verifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(AppConfig.jwt.secret))
        .withIssuer(AppConfig.jwt.issuer)
        .build()

    fun getVerifier(): JWTVerifier = verifier

    fun verify(token: String): DecodedJWT = verifier.verify(token)
}
