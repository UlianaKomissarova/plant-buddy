package dev.uliana.user.email.model.template

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.p

object EmailConfirmationTemplate : EmailTemplate<String> {
    override fun FlowContent.content(payload: String) {
        p {
            +"Чтобы подтвердить адрес электронной почты, пожалуйста, перейдите по ссылке ниже:"
        }
        p {
            a(href = payload) { +"Перейти для подтверждения почты" }
        }
        p {
            +"Если вы не запрашивали подтверждение, просто проигнорируйте это письмо."
        }
    }
}
