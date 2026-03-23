package dev.uliana.config

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.slf4j.LoggerFactory
import kotlin.reflect.KType

fun Application.configureSwagger() {
    val logger = LoggerFactory.getLogger("Swagger")

    install(OpenAPIGen) {
        serveOpenApiJson = true
        serveSwaggerUi = true

        info {
            title = AppConfig.swagger.title
            version = AppConfig.swagger.version
            description = AppConfig.swagger.description
        }

        replaceModule(DefaultSchemaNamer, getSchemaNamer())
    }

    logger.info("Swagger available at ${AppConfig.server.url}${AppConfig.swagger.url}/swagger-ui/index.html")
}

private fun getSchemaNamer(): SchemaNamer = object : SchemaNamer {
    private val regex = Regex("[A-Za-z0-9_.]+")

    override fun get(type: KType): String {
        return type
            .toString()
            .replace(regex) { it.value.split(".").last() }
            .replace(Regex(">|<|, "), "_")
    }
}
