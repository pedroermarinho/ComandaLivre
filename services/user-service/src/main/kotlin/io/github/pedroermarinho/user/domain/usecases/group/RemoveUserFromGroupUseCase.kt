package io.github.pedroermarinho.user.domain.usecases.group

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.user.domain.repositories.UserGroupRepository
import io.github.pedroermarinho.user.domain.usecases.user.SearchUserUseCase
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
