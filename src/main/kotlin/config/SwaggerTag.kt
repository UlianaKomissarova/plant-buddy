package dev.uliana.config

import com.papsign.ktor.openapigen.APITag

@Suppress("NonAsciiCharacters", "EnumEntryName")
enum class SwaggerTag(override val description: String = "") : APITag {
    Аутентификация,
    Регистрация,

}
