package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister.ClosingDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister.SessionDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ClosingEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.cashregister.ClosingResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeMapper
import org.springframework.stereotype.Component

@Component
class ClosingMapper(
    private val sessionMapper: SessionMapper,
    private val employeeMapper: EmployeeMapper,
) {
    fun toDTO(
        entity: ClosingEntity,
        session: SessionDTO,
        employee: EmployeeDTO,
    ) = ClosingDTO(
        id = entity.id,
        session = session,
        employee = employee,
        countedCash = entity.countedCash.value,
        countedCard = entity.countedCard.value,
        countedPix = entity.countedPix.value,
        countedOthers = entity.countedOthers.value,
        finalBalance = entity.finalBalance.value,
        finalBalanceExpected = entity.finalBalanceExpected.value,
        finalBalanceDifference = entity.finalBalanceDifference.value,
        observations = entity.observations?.value,
        auditData = entity.auditData,
        createdAt = entity.audit.createdAt,
    )

    fun toResponse(dto: ClosingDTO) =
        ClosingResponse(
            id = dto.id.publicId,
            session = sessionMapper.toResponse(dto.session),
            employee = employeeMapper.toResponse(dto.employee),
            countedCash = dto.countedCash,
            countedCard = dto.countedCard,
            countedPix = dto.countedPix,
            countedOthers = dto.countedOthers,
            finalBalance = dto.finalBalance,
            finalBalanceExpected = dto.finalBalanceExpected,
            finalBalanceDifference = dto.finalBalanceDifference,
            observations = dto.observations,
            auditData = dto.auditData,
            createdAt = dto.createdAt,
        )
}
