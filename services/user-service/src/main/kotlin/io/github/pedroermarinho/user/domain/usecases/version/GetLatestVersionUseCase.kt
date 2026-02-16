package io.github.pedroermarinho.user.domain.usecases.version

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.dtos.version.VersionDTO
import io.github.pedroermarinho.user.domain.enums.PlatformEnum
import io.github.pedroermarinho.user.domain.repositories.VersionRepository
import io.github.pedroermarinho.user.infra.mappers.VersionMapper
import io.github.pedroermarinho.user.infra.mappers.toDTO
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class GetLatestVersionUseCase(
    private val repository: VersionRepository,
    private val versionMapper: VersionMapper,
) {
    fun execute(platform: PlatformEnum): Result<VersionDTO> = repository.getLatestByPlatform(platform).map { versionMapper.toDTO(it) }
}
