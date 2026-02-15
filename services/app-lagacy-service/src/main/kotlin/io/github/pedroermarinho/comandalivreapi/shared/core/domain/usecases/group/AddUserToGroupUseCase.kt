package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.GroupEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user.AssignUserToGroupForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.UserGroupRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.request.user.AssignUserToGroupRequest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class AddUserToGroupUseCase(
    private val userGroupRepository: UserGroupRepository,
    private val searchGroupUseCase: SearchGroupUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val searchUserGroupUseCase: SearchUserGroupUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun execute(
        userId: UUID,
        groupId: UUID,
        form: AssignUserToGroupRequest?,
    ): Result<Unit> =
        runCatching {
            log.info { "Adicionando usuário $userId ao grupo $groupId" }

            val userId =
                searchUserUseCase
                    .getIdById(userId)
                    .getOrThrow()

            val groupId =
                searchGroupUseCase
                    .getIdByPublicId(groupId)
                    .getOrThrow()

            if (searchUserGroupUseCase.checkUserInGroup(userId.internalId, groupId.internalId)) {
                log.warn { "Usuário $userId já pertence ao grupo $groupId" }
                return Result.failure(Exception("Usuário já está no grupo"))
            }

            if (searchUserGroupUseCase.exists(userId.internalId, groupId.internalId)) {
                log.info { "Habilitando associação existente entre usuário $userId e grupo $groupId" }
                return userGroupRepository.changeEnabled(
                    userId = userId.internalId,
                    groupId = groupId.internalId,
                    enabled = true,
                )
            }

            val notes = form?.notes ?: "Adicionado de forma manual"
            log.info { "Criando nova associação entre usuário $userId e grupo $groupId. Notes: $notes" }

            return userGroupRepository
                .create(
                    AssignUserToGroupForm(
                        userId = userId.internalId,
                        featureGroupId = groupId.internalId,
                        notes = notes,
                        expiresAt = form?.expiresAt,
                    ),
                )
        }

    fun execute(
        userId: UUID,
        groups: List<GroupEnum>,
    ): Result<Unit> =
        runCatching {
            log.info { "Adicionando usuário $userId aos grupos $groups" }

            groups.map {
                val group = searchGroupUseCase.getByEnum(it).getOrThrow()
                log.info { "Adicionando usuário $userId ao grupo ${group.groupKey}" }
                this.execute(
                    userId = userId,
                    groupId = group.id.publicId,
                    form =
                        AssignUserToGroupRequest(
                            notes = "Adicionado automaticamente pelo sistema",
                            expiresAt = null,
                        ),
                )
            }
        }
}
