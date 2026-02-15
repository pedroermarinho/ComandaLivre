package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister.SessionStatusDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.cashregister.CashRegisterSessionStatusResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.SessionStatus
import org.springframework.stereotype.Component

@Component
class SessionStatusMapper {
    fun toDTO(entity: SessionStatus) =
        SessionStatusDTO(
            id = entity.id,
            key = entity.key.value,
            name = entity.name.value,
            description = entity.description,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: SessionStatusDTO) =
        CashRegisterSessionStatusResponse(
            id = dto.id.publicId,
            key = dto.key,
            name = dto.name,
            description = dto.description,
        )
}
