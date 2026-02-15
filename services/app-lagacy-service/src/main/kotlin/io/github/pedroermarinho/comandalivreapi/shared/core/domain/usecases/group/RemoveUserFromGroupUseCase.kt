package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.UserGroupRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class RemoveUserFromGroupUseCase(
    private val userGroupRepository: UserGroupRepository,
    private val searchGroupUseCase: SearchGroupUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val searchUserGroupUseCase: SearchUserGroupUseCase,
) {
    fun execute(
        userId: UUID,
        groupId: UUID,
    ): Result<Unit> =
        runCatching {
            val userId =
                searchUserUseCase
                    .getIdById(userId)
                    .getOrThrow()

            val groupId =
                searchGroupUseCase
                    .getIdByPublicId(groupId)
                    .getOrElse { return Result.failure(it) }

            if (!searchUserGroupUseCase.checkUserInGroup(
                    userId = userId.internalId,
                    groupId =groupId.internalId,
                )
            ) {
                return Result.failure(
                    BusinessLogicException("O usuário não está no grupo."),
                )
            }

            return userGroupRepository
                .changeEnabled(
                    userId = userId.internalId,
                    groupId = groupId.internalId,
                    enabled = false,
                )
        }
}
