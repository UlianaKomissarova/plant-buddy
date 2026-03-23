package dev.uliana.user.email.model

data class EmailConfig(
    val host: String,
    val port: String,
    val login: String,
    val password: String,
    val from: String,
    val name: String,
    val sslOnConnect: Boolean = true,
    val charset: String = "UTF-8",
)
