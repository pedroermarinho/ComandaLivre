package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeInviteDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeInviteEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeInviteMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchEmployeeInviteUseCase(
    private val employeeInviteRepository: EmployeeInviteRepository,
    private val currentUserService: CurrentUserService,
    private val searchUserUseCase: SearchUserUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchRoleTypeUseCase: SearchRoleTypeUseCase,
    private val searchEmployeeInviteStatusUseCase: SearchEmployeeInviteStatusUseCase,
    private val employeeInviteMapper: EmployeeInviteMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): Result<EmployeeInviteDTO> =
        runCatching {
            employeeInviteRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun getById(id: Int): Result<EmployeeInviteDTO> =
        runCatching {
            employeeInviteRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun getMyInvites(pageable: PageableDTO): Result<PageDTO<EmployeeInviteDTO>> =
        runCatching {
            val user = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
            return employeeInviteRepository.getBySub(pageable, user.sub).map { page -> page.map { convert(it).getOrThrow() } }
        }

    fun getByCompanyPublicId(
        companyPublicId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<EmployeeInviteDTO>> =
        runCatching {
            employeeInviteRepository.getByCompanyId(companyPublicId, pageable).map { page -> page.map { convert(it).getOrThrow() } }.getOrThrow()
        }

    private fun convert(entity: EmployeeInviteEntity): Result<EmployeeInviteDTO> =
        runCatching {
            employeeInviteMapper
                .toDTO(
                    entity = entity,
                ).getOrThrow()
        }.onFailure { log.error(it) { "Erro ao converter CompanyEntity para CompanyDTO para o ID da entidade: ${entity.id}" } }
}
