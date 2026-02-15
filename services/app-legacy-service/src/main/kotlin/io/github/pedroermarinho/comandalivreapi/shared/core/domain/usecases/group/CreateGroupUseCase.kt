package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.RequirePermissions
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.group.GroupDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.GroupEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FeatureEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user.FeatureGroupForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.GroupRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.GroupMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.toDTO
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@UseCase
class CreateGroupUseCase(
    private val groupRepository: GroupRepository,
    private val groupMapper: GroupMapper,
) {
    @RequirePermissions(
        all = [FeatureEnum.USER_ROLE_MANAGEMENT],
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    fun execute(form: FeatureGroupForm): Result<GroupDTO> =
        runCatching {
            val group =
                GroupEntity.createNew(
                    groupKey = form.groupKey,
                    name = form.name,
                    description = form.description,
                    createdAt = LocalDateTime.now(),
                )
            groupRepository
                .save(group)
                .map { groupMapper.toDTO(group) }
                .getOrThrow()
        }
}
