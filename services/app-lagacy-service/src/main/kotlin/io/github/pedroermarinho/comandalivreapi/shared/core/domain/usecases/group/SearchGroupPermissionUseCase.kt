package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.feature.FeatureDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.GroupPermissionRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.FeatureMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.toDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchGroupPermissionUseCase(
    private val groupPermissionRepository: GroupPermissionRepository,
    private val featureMapper: FeatureMapper,
) {
    fun checkFeatureInGroup(
        featureId: Int,
        groupId: Int,
    ): Boolean =
        groupPermissionRepository.checkFeatureInGroup(
            featureId = featureId,
            featureGroupId = groupId,
        )

    fun exists(
        featureId: Int,
        groupId: Int,
    ): Boolean = groupPermissionRepository.exists(featureId, groupId)

    fun getAll(
        groupId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<FeatureDTO>> = groupPermissionRepository.getAll(groupId, pageable).map { it.map { entity -> featureMapper.toDTO(entity) } }
}
