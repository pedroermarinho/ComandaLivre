package io.github.pedroermarinho.comandalivre.infra.mappers

import comandalivre.tables.records.OrderItemsRecord
import comandalivre.tables.records.OrderStatusRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.OrderEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.order.OrderResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandId
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderNotes
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductId
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.MonetaryValue
import io.github.pedroermarinho.shared.valueobject.UserId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class OrderPersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val orderStatusPersistenceMapper: OrderStatusPersistenceMapper,
) {
    fun toRecord(entity: OrderEntity): Result<OrderItemsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            OrderItemsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                commandId = entity.commandId.value,
                productId = entity.productId.value,
                statusId = entity.status.id.internalId,
                notes = entity.notes?.value,
                basePriceAtOrder = entity.basePriceAtOrder?.value,
                totalModifiersPriceAtOrder = entity.totalModifiersPriceAtOrder?.value,
                cancellationReason = entity.cancellationReason,
                cancelledByUserId = entity.cancelledByUserId?.value,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        orderItemsRecord: OrderItemsRecord,
        orderStatusRecord: OrderStatusRecord,
    ): Result<OrderEntity> =
        errorDataConversion {
            OrderEntity(
                id =
                    EntityId(
                        internalId = orderItemsRecord.id!!,
                        publicId = orderItemsRecord.publicId,
                    ),
                commandId = CommandId.restore(orderItemsRecord.commandId),
                productId = ProductId.restore(orderItemsRecord.productId),
                status = orderStatusPersistenceMapper.toEntity(orderStatusRecord).getOrThrow(),
                notes = orderItemsRecord.notes?.let { OrderNotes.restore(it) },
                basePriceAtOrder = orderItemsRecord.basePriceAtOrder?.let { MonetaryValue.restore(it) },
                totalModifiersPriceAtOrder = orderItemsRecord.totalModifiersPriceAtOrder?.let { MonetaryValue.restore(it) },
                cancellationReason = orderItemsRecord.cancellationReason,
                cancelledByUserId = orderItemsRecord.cancelledByUserId?.let { UserId.restore(it) },
                audit =
                    EntityAudit(
                        createdAt = orderItemsRecord.createdAt!!,
                        updatedAt = orderItemsRecord.updatedAt!!,
                        deletedAt = orderItemsRecord.deletedAt,
                        createdBy = orderItemsRecord.createdBy,
                        updatedBy = orderItemsRecord.updatedBy,
                        version = orderItemsRecord.version!!,
                    ),
            )
        }
}

@Component
class OrderMapper(
    private val orderStatusMapper: OrderStatusMapper,
    private val productMapper: ProductMapper,
    private val commandMapper: CommandMapper,
) {
    fun toDTO(
        entity: OrderEntity,
        command: CommandDTO,
        product: ProductDTO,
    ) = OrderDTO(
        id = entity.id,
        status = orderStatusMapper.toDTO(entity.status),
        product = product,
        notes = entity.notes?.value,
        priorityLevel = entity.priorityLevel.value,
        cancellationReason = entity.cancellationReason,
        basePriceAtOrder = entity.basePriceAtOrder?.value,
        totalModifiersPriceAtOrder = entity.totalModifiersPriceAtOrder?.value,
        createdAt = entity.audit.createdAt,
        command = command,
    )

    fun toResponse(dto: OrderDTO) =
        OrderResponse(
            id = dto.id.publicId,
            status = orderStatusMapper.toResponse(dto.status),
            product = productMapper.toResponse(dto.product),
            priorityLevel = dto.priorityLevel,
            cancellationReason = dto.cancellationReason,
            itemPriceAtOrder = dto.basePriceAtOrder,
            totalModifiersPrice = dto.totalModifiersPriceAtOrder,
            createdAt = dto.createdAt,
            command = commandMapper.toResponse(dto.command),
            notes = dto.notes,
        )
}
