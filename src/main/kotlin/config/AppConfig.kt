package dev.uliana.config

import io.ktor.server.config.ApplicationConfig

object AppConfig {
    private const val API_VERSION = "/api/v1"

    lateinit var postgres: PostgresConfig
    lateinit var jwt: JwtConfig
    lateinit var mail: MailConfig
    lateinit var swagger: SwaggerConfig
    lateinit var server: ServerConfig

    fun init(config: ApplicationConfig) {
        postgres = PostgresConfig(
            jdbcDriver = config.property("postgres.jdbcDriver").getString(),
            jdbcUrl = config.property("postgres.jdbcUrl").getString(),
            user = config.property("postgres.user").getString(),
            password = config.property("postgres.password").getString(),
            maxPoolSize = config.property("postgres.maxPoolSize").getString().toInt(),
            minimumIdle = config.property("postgres.minimumIdle").getString().toInt(),
            maxLifetime = config.property("postgres.maxLifetime").getString().toLong(),
            idleTimeout = config.property("postgres.idleTimeout").getString().toLong(),
            connectionTimeout = config.property("postgres.connectionTimeout").getString().toLong(),
        )

        jwt = JwtConfig(
            secret = config.property("jwt.secret").getString(),
            issuer = config.property("jwt.issuer").getString(),
            audience = config.property("jwt.audience").getString(),
            accessTokenTtlMinutes = config.property("jwt.accessTokenTtlMinutes").getString().toLong(),
            refreshTokenTtlDays = config.property("jwt.refreshTokenTtlDays").getString().toLong(),
        )

        mail = MailConfig(
            host = config.property("mail.host").getString(),
            port = config.property("mail.port").getString().toInt(),
            username = config.property("mail.username").getString(),
            password = config.property("mail.password").getString(),
            from = config.property("mail.from").getString(),
            fromName = config.property("mail.fromName").getString(),
        )

        swagger = SwaggerConfig(
            url = config.propertyOrNull("swagger.url")?.getString() ?: "/swagger",
            title = config.propertyOrNull("swagger.title")?.getString() ?: "PlantApp API",
            version = config.propertyOrNull("swagger.version")?.getString() ?: "1.0.0",
            description = config.propertyOrNull("swagger.description")?.getString() ?: "",
        )

        server = ServerConfig(
            url = config.propertyOrNull("server.url")?.getString() ?: "http://localhost:8080",
            apiVersion = API_VERSION,
        )
    }

    val baseUrl: String get() = server.url + server.apiVersion
}

data class PostgresConfig(
    val jdbcDriver: String,
    val jdbcUrl: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int,
    val minimumIdle: Int,
    val maxLifetime: Long,
    val idleTimeout: Long,
    val connectionTimeout: Long,
)

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val accessTokenTtlMinutes: Long,
    val refreshTokenTtlDays: Long,
)

data class MailConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val from: String,
    val fromName: String,
)

data class SwaggerConfig(
    val url: String,
    val title: String,
    val version: String,
    val description: String,
)

data class ServerConfig(
    val url: String,
    val apiVersion: String,
)
