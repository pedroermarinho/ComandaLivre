package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers

import comandalivre.tables.records.OrderStatusRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderStatusDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.order.OrderStatusResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderStatus
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeKey
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeName
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class OrderStatusPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: OrderStatus): Result<OrderStatusRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            OrderStatusRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                key = entity.key.value,
                name = entity.name.value,
                description = entity.description,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: OrderStatusRecord): Result<OrderStatus> =
        errorDataConversion {
            OrderStatus(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                key = TypeKey.restore(record.key),
                name = TypeName.restore(record.name),
                description = record.description,
                audit =
                    EntityAudit(
                        createdAt = record.createdAt!!,
                        updatedAt = record.updatedAt!!,
                        deletedAt = record.deletedAt,
                        createdBy = record.createdBy,
                        updatedBy = record.updatedBy,
                        version = record.version!!,
                    ),
            )
        }
}

@Component
class OrderStatusMapper {
    fun toDTO(entity: OrderStatus) =
        OrderStatusDTO(
            id = entity.id,
            key = entity.key.value,
            name = entity.name.value,
            description = entity.description,
        )

    fun toResponse(dto: OrderStatusDTO) =
        OrderStatusResponse(
            id = dto.id.publicId,
            name = dto.name,
            key = dto.key,
            description = dto.description,
        )
}
