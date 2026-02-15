package io.github.pedroermarinho.comandalivreapi.shared.core.domain.listeners

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.CompanyCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.EmployeeInviteCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.NewUserRegisteredEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.EmailService
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.EmailTemplate
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EmailNotificationListener(
    private val emailService: EmailService,
) {
    private val log = KotlinLogging.logger {}

    @Async
    @EventListener
    fun onEmployeeInviteCreated(event: EmployeeInviteCreatedEvent) {
        val subject = "Comanda Livre - Convite para se juntar à equipe"

        val mainContent =
            """
            <p>Olá,</p>
            <p>Você foi convidado(a) para se juntar à equipe do estabelecimento <strong>${event.companyName}</strong> com o cargo de <strong>${event.roleName}</strong>.</p>
            <p>Para aceitar o convite, por favor, clique no botão abaixo e siga as instruções:</p>
            <p style="text-align: center; margin: 30px 0;">
                <a href="https://comandalivre.com.br/invite?token=${event.inviteToken}" class="button">Aceitar Convite</a>
            </p>
            <p>Se o botão não funcionar, copie e cole o seguinte link no seu navegador:<br>
            <a href="https://comandalivre.com.br/invite?token=${event.inviteToken}">https://comandalivre.com.br/invite?token=${event.inviteToken}</a></p>
            """.trimIndent()

        val body =
            EmailTemplate.create(
                title = "Você foi convidado!",
                mainContentHtml = mainContent,
            )

        emailService.sendEmail(
            to = event.recipientEmail,
            subject = subject,
            body = body,
            isHtml = true,
        )
    }

    @Async
    @EventListener
    fun onNewUserRegistered(event: NewUserRegisteredEvent) {
        log.info { "Listener de E-mail acionado para o novo usuário ${event.email}" }

        val subject = "Seja bem-vindo(a) ao Comanda Livre!"

        val mainContent =
            """
            <h3>Olá, ${event.name}!</h3>
            <p>É um prazer ter você conosco no <strong>Comanda Livre</strong>!</p>
            <p>Sua conta foi criada com sucesso. Agora você pode explorar restaurantes, gerenciar suas comandas e ter uma experiência gastronômica muito mais simples e digital.</p>
            <p style="text-align: center; margin: 30px 0;">
                <a href="https://comandalivre.com.br/login" class="button">Acessar Minha Conta</a>
            </p>
            """.trimIndent()

        val body =
            EmailTemplate.create(
                title = "Bem-vindo(a)!",
                mainContentHtml = mainContent,
            )

        emailService.sendEmail(
            to = event.email,
            subject = subject,
            body = body,
            isHtml = true,
        )
    }

    @Async
    @EventListener
    fun onCompanyCreated(event: CompanyCreatedEvent) {
        log.info { "Listener de E-mail acionado para a nova empresa '${event.companyName}' (ID: ${event.companyPublicId})" }

        val subject = "Parabéns! Sua empresa '${event.companyName}' foi criada no Comanda Livre"

        val mainContent =
            """
            <h3>Olá, ${event.ownerName}!</h3>
            <p>Parabéns! Sua empresa, <strong>${event.companyName}</strong>, foi registrada com sucesso em nossa plataforma.</p>
            <p>Este é um passo importante para simplificar e digitalizar a gestão do seu negócio. Agora você já pode começar a configurar seu estabelecimento, cadastrar seus produtos e preparar tudo para receber seus clientes.</p>

            <h4>O que fazer agora?</h4>
            <ul>
                <li>Acesse o painel de controle para configurar os detalhes da sua empresa.</li>
                <li>Cadastre seu cardápio ou portfólio de serviços.</li>
                <li>Convide sua equipe para colaborar na plataforma.</li>
            </ul>

            <p style="text-align: center; margin: 30px 0;">
                <a href="https://comandalivre.com.br/dashboard/${event.companyPublicId}" class="button">Acessar Painel de Controle</a>
            </p>

            <p>Estamos muito felizes em ter você conosco!</p>
            """.trimIndent()

        val body =
            EmailTemplate.create(
                title = "Empresa Criada com Sucesso!",
                mainContentHtml = mainContent,
            )

        emailService.sendEmail(
            to = event.ownerEmail,
            subject = subject,
            body = body,
            isHtml = true,
        )
    }
}
