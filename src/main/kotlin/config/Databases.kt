package dev.uliana.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.util.IsolationLevel
import io.ktor.server.application.Application
import io.ktor.server.application.log
import java.sql.Connection
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

/**
 * Makes a connection to a Postgres database.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */
fun Application.connectToPostgres(): HikariDataSource {
    val config = AppConfig.postgres

    log.info("Connecting to postgres database at ${config.jdbcUrl}")

    val hikariConfig = HikariConfig().apply {
        driverClassName = config.jdbcDriver
        jdbcUrl = config.jdbcUrl
        username = config.user
        password = config.password
        maximumPoolSize = config.maxPoolSize
        minimumIdle = config.minimumIdle
        maxLifetime = config.maxLifetime
        idleTimeout = config.idleTimeout
        connectionTimeout = config.connectionTimeout
        isAutoCommit = false
        transactionIsolation = IsolationLevel.TRANSACTION_REPEATABLE_READ.name
        validate()
    }

    val dataSource = HikariDataSource(hikariConfig)

    Database.connect(dataSource)

    Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .outOfOrder(true)
        .load()
        .migrate()

    log.info("Database connected and migrations applied successfully")

    return dataSource
}
