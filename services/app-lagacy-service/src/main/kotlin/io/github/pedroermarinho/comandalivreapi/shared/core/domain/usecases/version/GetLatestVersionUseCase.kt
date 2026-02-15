package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.version

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.version.VersionDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.PlatformEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.VersionRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.VersionMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.toDTO
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class GetLatestVersionUseCase(
    private val repository: VersionRepository,
    private val versionMapper: VersionMapper,
) {
    fun execute(platform: PlatformEnum): Result<VersionDTO> = repository.getLatestByPlatform(platform).map { versionMapper.toDTO(it) }
}
