package io.github.pedroermarinho.user.domain.usecases.feature

import io.github.pedroermarinho.user.domain.annotations.RequirePermissions
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.dtos.feature.FeatureDTO
import io.github.pedroermarinho.user.domain.dtos.feature.FeatureFilterDTO
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.enums.FeatureEnum
import io.github.pedroermarinho.user.domain.repositories.FeatureRepository
import io.github.pedroermarinho.user.infra.mappers.FeatureMapper
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchFeatureUseCase(
    private val featureRepository: FeatureRepository,
    private val featureMapper: FeatureMapper,
) {
    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    fun getByKey(key: String): Result<FeatureDTO> = featureRepository.getByKey(key).map { featureMapper.toDTO(it) }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    fun getById(id: UUID): Result<FeatureDTO> = featureRepository.getById(id).map { featureMapper.toDTO(it) }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    fun getAll(
        pageable: PageableDTO,
        filter: FeatureFilterDTO,
    ): Result<PageDTO<FeatureDTO>> = featureRepository.getAll(pageable, filter).map { it.map { entity -> featureMapper.toDTO(entity) } }
}
