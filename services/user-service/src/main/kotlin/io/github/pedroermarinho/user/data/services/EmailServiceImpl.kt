package io.github.pedroermarinho.user.data.services

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.user.domain.enums.FeatureSystemFlagEnum
import io.github.pedroermarinho.user.domain.services.EmailService
import io.github.pedroermarinho.user.domain.usecases.featureflag.StatusFeatureFlagUseCase
import io.github.pedroermarinho.user.infra.properties.MailProperties
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    private val statusFeatureFlagUseCase: StatusFeatureFlagUseCase,
    private val mailProperties: MailProperties,
) : EmailService {
    private val log = KotlinLogging.logger {}

    override fun sendEmail(
        to: String,
        subject: String,
        body: String,
        isHtml: Boolean,
    ) {
        if (!statusFeatureFlagUseCase.isEnabled(FeatureSystemFlagEnum.EMAIL_SENDING)) {
            log.info { "Envio de email desabilitado" }
            return
        }

        try {
            log.info { "Enviando email para $to" }

            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setFrom(mailProperties.username, "Comanda Livre")
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(body, isHtml)

            mailSender.send(message)
            log.info { "Email enviado para $to" }
        } catch (e: Exception) {
            log.error(e) { "Falha ao enviar e-mail para $to" }
        }
    }
}
