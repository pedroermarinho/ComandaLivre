package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchEmployeeUseCase(
    private val employeeRepository: EmployeeRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val searchRoleTypeUseCase: SearchRoleTypeUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val employeeMapper: EmployeeMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getMyEmployees(pageable: PageableDTO): Result<PageDTO<EmployeeDTO>> =
        runCatching {
            val userId = currentUserUseCase.getUserId().getOrThrow()
            return employeeRepository.getByUserId(pageable, userId).map { page -> page.map { convert(it).getOrThrow() } }
        }

    fun getById(id: UUID): Result<EmployeeDTO> =
        runCatching {
            employeeRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun getById(id: Int): Result<EmployeeDTO> =
        runCatching {
            employeeRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun getByCompanyId(companyId: Int): Result<EmployeeDTO> =
        runCatching {
            val userId = currentUserUseCase.getUserId().getOrThrow()
            employeeRepository.getByCompanyId(userId = userId, companyId = companyId).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun getByCompanyId(companyId: UUID): Result<EmployeeDTO> =
        runCatching {
            val companyInternalId = searchCompanyUseCase.getIdById(companyId).getOrThrow()
            val userId = currentUserUseCase.getUserId().getOrThrow()
            employeeRepository.getByCompanyId(userId = userId, companyId = companyInternalId).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun getAll(
        pageable: PageableDTO,
        companyPublicId: UUID,
    ): Result<PageDTO<EmployeeDTO>> =
        runCatching {
            employeeRepository.getByCompanyId(companyPublicId, pageable).map { page -> page.map { convert(it).getOrThrow() } }.getOrThrow()
        }

    fun isEmployeeOfCompany(
        userId: Int,
        companyId: Int,
    ): Result<Boolean> = employeeRepository.isEmployeeOfCompany(userId, companyId)

    fun checkEmployeeOfCompany(
        userId: Int,
        companyId: Int,
    ): Result<Unit> {
        val isEmployeeOfCompany = this.isEmployeeOfCompany(userId = userId, companyId = companyId).getOrThrow()
        if (isEmployeeOfCompany) {
            log.error { "Usuário já $userId é funcionário do restaurante $companyId" }
            return Result.failure(
                BusinessLogicException("Usuário já é funcionário do restaurante"),
            )
        }

        return Result.success(Unit)
    }

    fun hasActiveCompanyRelation(): Result<Boolean> =
        runCatching {
            val userId = currentUserUseCase.getUserId().getOrThrow()
            return employeeRepository.hasActiveCompanyRelation(userId)
        }

    fun getByUserIdAndCompanyId(
        userId: Int,
        companyId: Int,
    ): Result<EmployeeDTO> =
        runCatching {
            employeeRepository.getByUserIdAndCompanyId(userId, companyId).map { convert(it).getOrThrow() }.getOrThrow()
        }

    private fun convert(entity: EmployeeEntity): Result<EmployeeDTO> =
        runCatching {
            employeeMapper.toDTO(
                entity = entity,
                user = searchUserUseCase.getById(entity.userId.value).getOrThrow(),
                company = searchCompanyUseCase.getById(entity.companyId.value).getOrThrow(),
            )
        }.onFailure { log.error(it) { "Erro ao converter CompanyEntity para CompanyDTO para o ID da entidade: ${entity.id}" } }
}
