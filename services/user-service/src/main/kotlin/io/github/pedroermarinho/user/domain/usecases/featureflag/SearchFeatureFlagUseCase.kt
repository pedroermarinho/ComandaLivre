package io.github.pedroermarinho.user.domain.usecases.featureflag

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.dtos.featureflag.FeatureFlagDTO
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.FeatureFlagEntity
import io.github.pedroermarinho.user.domain.repositories.FeatureFlagRepository
import io.github.pedroermarinho.user.infra.mappers.FeatureFlagMapper
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchFeatureFlagUseCase(
    private val featureFlagRepository: FeatureFlagRepository,
    private val featureFlagMapper: FeatureFlagMapper,
) {
    fun getById(id: Int): Result<FeatureFlagDTO> = featureFlagRepository.getById(id).map { featureFlagMapper.toDTO(it) }

    fun getById(publicId: UUID): Result<FeatureFlagDTO> = featureFlagRepository.getById(publicId).map { featureFlagMapper.toDTO(it) }

    fun getEntityById(publicId: UUID): Result<FeatureFlagEntity> = featureFlagRepository.getById(publicId)

    fun getAll(pageable: PageableDTO): Result<PageDTO<FeatureFlagDTO>> = featureFlagRepository.getAll(pageable).map { it.map { entity -> featureFlagMapper.toDTO(entity) } }
}
