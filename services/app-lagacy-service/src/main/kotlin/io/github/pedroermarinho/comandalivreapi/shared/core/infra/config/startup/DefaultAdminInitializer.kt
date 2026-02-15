package io.github.pedroermarinho.comandalivreapi.shared.core.infra.config.startup

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.GroupEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group.AddUserToGroupUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group.SearchGroupUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group.SearchUserGroupUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.properties.AppProperties
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DefaultAdminInitializer(
    private val searchUserUseCase: SearchUserUseCase,
    private val addUserToGroupUseCase: AddUserToGroupUseCase,
    private val searchUserGroupUseCase: SearchUserGroupUseCase,
    private val searchGroupUseCase: SearchGroupUseCase,
    private val appProperties: AppProperties,
) : ApplicationRunner {
    private val log = KotlinLogging.logger {}

    override fun run(args: ApplicationArguments?) {
        if (appProperties.defaultAdminEmails.isBlank()) {
            log.info { "Nenhum e-mail de administrador padrão configurado." }
            return
        }

        val emails = appProperties.defaultAdminEmails.split(",").mapNotNull { it.trim().lowercase().takeIf(String::isNotBlank) }

        for (email in emails) {
            val user = searchUserUseCase.getByEmail(email).getOrNull()
            val group = searchGroupUseCase.getByEnum(GroupEnum.ADMIN_SYSTEM).getOrNull()
            if (user == null) {
                log.warn { "Usuário com e-mail $email não encontrado. Ignorando." }
                continue
            }

            if (group == null) {
                log.warn { "Grupo ${GroupEnum.ADMIN_SYSTEM.name} não encontrado. Ignorando adição do usuário $email a este grupo." }
                continue
            }

            if (searchUserGroupUseCase.checkUserInGroup(user.id.internalId, group.id.internalId)) {
                log.info { "Usuário ${user.name} já pertence ao grupo ${GroupEnum.ADMIN_SYSTEM.name}. Ignorando." }
                continue
            }

            val result = addUserToGroupUseCase.execute(user.id.publicId, listOf(GroupEnum.ADMIN_SYSTEM))

            if (result.isSuccess) {
                log.info { "Usuário ${user.name} adicionado ao grupo ${GroupEnum.ADMIN_SYSTEM.name} com sucesso." }
            } else {
                log.error(result.exceptionOrNull()) { "Erro ao adicionar ${user.name} ao grupo ${GroupEnum.ADMIN_SYSTEM.name}: ${result.exceptionOrNull()?.message}" }
            }
        }
    }
}
