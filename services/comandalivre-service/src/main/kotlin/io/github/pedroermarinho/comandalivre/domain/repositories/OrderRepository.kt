package io.github.pedroermarinho.comandalivre.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderFilterDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.OrderEntity
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface OrderRepository {
    fun getById(orderId: Int): Result<OrderEntity>

    fun getById(publicId: UUID): Result<OrderEntity>

    fun getAll(
        pageable: PageableDTO,
        filter: OrderFilterDTO,
    ): Result<PageDTO<OrderEntity>>

    fun getAll(commandId: Int): Result<List<OrderEntity>>

    fun delete(orderId: Int): Result<Unit>

    fun save(entity: OrderEntity): Result<EntityId>
}
