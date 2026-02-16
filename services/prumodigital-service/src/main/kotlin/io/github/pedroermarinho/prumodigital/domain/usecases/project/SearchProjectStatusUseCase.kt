package io.github.pedroermarinho.prumodigital.domain.usecases.project

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.enums.ProjectStatusEnum
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.ProjectStatusRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.ProjectStatusMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchProjectStatusUseCase(
    private val projectStatusRepository: ProjectStatusRepository,
    private val projectStatusMapper: ProjectStatusMapper,
) {
    fun getById(id: UUID): Result<ProjectStatusDTO> =
        runCatching {
            projectStatusRepository.getById(id).getOrThrow().let { projectStatusMapper.toDTO(it) }
        }

    fun getById(id: Int): Result<ProjectStatusDTO> =
        runCatching {
            projectStatusRepository.getById(id).getOrThrow().let { projectStatusMapper.toDTO(it) }
        }

    fun getByKey(key: String): Result<ProjectStatusDTO> =
        runCatching {
            projectStatusRepository.getByKey(key).getOrThrow().let { projectStatusMapper.toDTO(it) }
        }

    fun getByEnum(enum: ProjectStatusEnum): Result<ProjectStatusDTO> = getByKey(enum.value)

    fun getAll(pageable: PageableDTO): Result<PageDTO<ProjectStatusDTO>> =
        projectStatusRepository.getAll(pageable).map { page -> page.map { entity -> projectStatusMapper.toDTO(entity) } }

    fun getAll(): Result<List<ProjectStatusDTO>> =
        runCatching {
            projectStatusRepository.getAll().getOrThrow().map { entity -> projectStatusMapper.toDTO(entity) }
        }
}
