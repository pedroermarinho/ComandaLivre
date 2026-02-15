package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.GroupPermissionRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.feature.SearchFeatureUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class RemoveFeatureFromGroupUseCase(
    private val searchGroupUseCase: SearchGroupUseCase,
    private val searchFeatureUseCase: SearchFeatureUseCase,
    private val groupPermissionRepository: GroupPermissionRepository,
    private val searchGroupPermissionUseCase: SearchGroupPermissionUseCase,
) {
    fun execute(
        groupId: UUID,
        featureId: UUID,
    ): Result<Unit> {
        val group =
            searchGroupUseCase
                .getById(groupId)
                .getOrElse { return Result.failure(it) }

        val feature =
            searchFeatureUseCase
                .getById(featureId)
                .getOrElse { return Result.failure(it) }

        if (!searchGroupPermissionUseCase.checkFeatureInGroup(
                featureId = feature.id.internalId,
                groupId = group.id.internalId,
            )
        ) {
            return Result.failure(
                BusinessLogicException("O recurso ${feature.name} não está em grupo ${group.name}"),
            )
        }

        return groupPermissionRepository
            .changeEnabled(
                groupId = group.id.internalId,
                featureId = feature.id.internalId,
                enabled = false,
            )
    }
}
