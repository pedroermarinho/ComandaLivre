package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeInviteStatusDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.EmployeeInviteEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteStatusRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeInviteStatus
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeInviteStatusMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchEmployeeInviteStatusUseCase(
    private val employeeInviteStatusRepository: EmployeeInviteStatusRepository,
    private val employeeInviteStatusMapper: EmployeeInviteStatusMapper,
) {
    fun getByPublicId(publicId: UUID): Result<EmployeeInviteStatusDTO> = employeeInviteStatusRepository.getById(publicId).map { employeeInviteStatusMapper.toDTO(it) }

    fun getAll(pageable: PageableDTO): Result<PageDTO<EmployeeInviteStatusDTO>> =
        employeeInviteStatusRepository.getAll(pageable).map {
            it.map { entity -> employeeInviteStatusMapper.toDTO(entity) }
        }

    fun getById(id: Int): Result<EmployeeInviteStatusDTO> = employeeInviteStatusRepository.getById(id).map { employeeInviteStatusMapper.toDTO(it) }

    fun getByKey(key: String): Result<EmployeeInviteStatus> = employeeInviteStatusRepository.getByKey(key)

    fun getByEnum(enum: EmployeeInviteEnum): Result<EmployeeInviteStatus> = getByKey(enum.value)
}
