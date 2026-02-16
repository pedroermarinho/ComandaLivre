package io.github.pedroermarinho.user.domain.services

interface EmailService {
    fun sendEmail(
        to: String,
        subject: String,
        body: String,
        isHtml: Boolean = true,
    )
}
