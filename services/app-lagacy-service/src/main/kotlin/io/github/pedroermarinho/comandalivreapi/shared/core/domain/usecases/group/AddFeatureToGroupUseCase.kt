package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user.GrantPermissionForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.GroupPermissionRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.feature.SearchFeatureUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class AddFeatureToGroupUseCase(
    private val searchGroupUseCase: SearchGroupUseCase,
    private val searchFeatureUseCase: SearchFeatureUseCase,
    private val groupPermissionRepository: GroupPermissionRepository,
    private val searchGroupPermissionUseCase: SearchGroupPermissionUseCase,
) {
    fun execute(
        groupPublicId: UUID,
        featurePublicId: UUID,
    ): Result<Unit> =
        runCatching {
            val group =
                searchGroupUseCase
                    .getById(groupPublicId)
                    .getOrThrow()

            val feature =
                searchFeatureUseCase
                    .getById(featurePublicId)
                    .getOrThrow()

            if (searchGroupPermissionUseCase.checkFeatureInGroup(
                    featureId = feature.id.internalId,
                    groupId = group.id.internalId,
                )
            ) {
                return Result.failure(
                    BusinessLogicException("A funcionalidade ${feature.name} já está atribuída ao grupo ${group.name}."),
                )
            }

            if (searchGroupPermissionUseCase.exists(
                    featureId = feature.id.internalId,
                    groupId = group.id.internalId,
                )
            ) {
                return groupPermissionRepository.changeEnabled(
                    groupId = group.id.internalId,
                    featureId = feature.id.internalId,
                    enabled = true,
                )
            }

            return groupPermissionRepository
                .create(
                    GrantPermissionForm(
                        groupId = group.id.internalId,
                        featureId = feature.id.internalId,
                    ),
                )
        }
}
