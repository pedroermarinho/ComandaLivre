package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister.SessionDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.SessionEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.cashregister.SessionResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.UserMapper
import org.springframework.stereotype.Component

@Component
class SessionMapper(
    private val sessionStatusMapper: SessionStatusMapper,
    private val employeeMapper: EmployeeMapper,
    private val userMapper: UserMapper,
) {
    fun toDTO(
        entity: SessionEntity,
        employee: EmployeeDTO,
        openedByUser: UserDTO?,
        closedByUser: UserDTO?,
    ) = SessionDTO(
        id = entity.id,
        employee = employee,
        openedByUser = openedByUser,
        closedByUser = closedByUser,
        initialValue = entity.initialValue.value,
        status = sessionStatusMapper.toDTO(entity.status),
        startedAt = entity.startedAt,
        endedAt = entity.endedAt,
        notes = entity.notes?.value,
        createdAt = entity.audit.createdAt,
    )

    fun toResponse(dto: SessionDTO) =
        SessionResponse(
            id = dto.id.publicId,
            employee = employeeMapper.toResponse(dto.employee),
            openedByUser = dto.openedByUser?.let { userMapper.toResponse(it) },
            closedByUser = dto.closedByUser?.let { userMapper.toResponse(it) },
            initialValue = dto.initialValue,
            status = sessionStatusMapper.toResponse(dto.status),
            startedAt = dto.startedAt,
            endedAt = dto.endedAt,
            notes = dto.notes,
            createdAt = dto.createdAt,
        )
}
