package io.github.pedroermarinho.comandalivreapi.shared.core.domain.services

interface EmailService {
    fun sendEmail(
        to: String,
        subject: String,
        body: String,
        isHtml: Boolean = true,
    )
}
