package io.github.pedroermarinho.user.domain.usecases.group

import io.github.pedroermarinho.user.domain.annotations.RequirePermissions
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.dtos.group.GroupDTO
import io.github.pedroermarinho.user.domain.entities.GroupEntity
import io.github.pedroermarinho.user.domain.enums.FeatureEnum
import io.github.pedroermarinho.user.domain.forms.user.FeatureGroupForm
import io.github.pedroermarinho.user.domain.repositories.GroupRepository
import io.github.pedroermarinho.user.infra.mappers.GroupMapper
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
