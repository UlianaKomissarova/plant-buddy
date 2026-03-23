package dev.uliana.user.email.model.template

import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.stream.createHTML

interface EmailTemplate<T> {
    fun message(payload: T): String =
        createHTML().html {
            body {
                greeting()
                content(payload)
                footer()
            }
        }

    @HtmlTagMarker
    fun FlowContent.greeting() {
        p { +"Здравствуйте!" }
    }

    @HtmlTagMarker
    fun FlowContent.content(payload: T)

    @HtmlTagMarker
    fun FlowContent.footer() {
        p {
            br { +"С уважением," }
            br { +"команда Plant Buddy" }
        }
    }
}
