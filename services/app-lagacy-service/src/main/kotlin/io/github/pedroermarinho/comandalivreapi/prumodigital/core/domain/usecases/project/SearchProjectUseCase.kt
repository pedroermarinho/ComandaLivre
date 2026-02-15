package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectCountByStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ProjectEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.ProjectRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.ProjectMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address.SearchAddressUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchProjectStatusUseCase: SearchProjectStatusUseCase,
    private val searchAddressUseCase: SearchAddressUseCase,
    private val projectMapper: ProjectMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getAll(pageable: PageableDTO): Result<PageDTO<ProjectDTO>> =
        runCatching {
            projectRepository.getAll(pageable).map { page -> page.map { convert(it).getOrThrow() } }.getOrThrow()
        }

    fun getById(id: UUID): Result<ProjectDTO> =
        runCatching {
            convert(projectRepository.getById(id).getOrThrow()).getOrThrow()
        }

    fun getById(id: Int): Result<ProjectDTO> =
        runCatching {
            convert(projectRepository.getById(id).getOrThrow()).getOrThrow()
        }

    fun getByCodeAndCompanyId(
        code: String,
        companyId: Int,
    ): Result<ProjectDTO> =
        runCatching {
            convert(projectRepository.getByCodeAndCompanyId(code, companyId).getOrThrow()).getOrThrow()
        }

    fun existsByCodeAndCompanyId(
        code: String,
        companyId: Int,
    ): Boolean = projectRepository.getByCodeAndCompanyId(code, companyId).isSuccess

    fun getProjectCountByStatus(): Result<List<ProjectCountByStatusDTO>> = projectRepository.getProjectCountByStatus()

    private fun convert(entity: ProjectEntity): Result<ProjectDTO> =
        runCatching {
            projectMapper.toDTO(
                entity = entity,
                company = searchCompanyUseCase.getById(entity.companyId).getOrThrow(),
                address = entity.addressId?.let { searchAddressUseCase.getById(it).getOrNull() },
                projectStatus = searchProjectStatusUseCase.getById(entity.projectStatusId).getOrThrow(),
            )
        }.onFailure { log.error(it) { "Erro ao converter CompanyEntity para CompanyDTO para o ID da entidade: ${entity.id}" } }
}
