package dev.uliana

import com.papsign.ktor.openapigen.route.apiRouting
import dev.uliana.config.AppConfig
import dev.uliana.config.configureFrameworks
import dev.uliana.config.configureHTTP
import dev.uliana.config.configureSecurity
import dev.uliana.config.configureSerialization
import dev.uliana.config.configureStatusPages
import dev.uliana.config.configureSwagger
import dev.uliana.config.connectToPostgres
import dev.uliana.config.serviceRouting
import dev.uliana.config.swaggerRouting
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.log
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val config = environment.config

    AppConfig.init(config = config)

    configureHTTP()
    configureSwagger()
    configureSerialization()
    val dataSource = connectToPostgres()

    monitor.subscribe(ApplicationStopped) {
        dataSource.close()
        log.info("Database connection pool closed")
    }

    configureSecurity()
    configureFrameworks()
    configureStatusPages()

    apiRouting { swaggerRouting() }

    routing { serviceRouting() }
}
