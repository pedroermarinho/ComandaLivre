package io.github.pedroermarinho.comandalivre.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderStatus
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId

interface OrderStatusRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<OrderStatus>>

    fun getAll(): Result<List<OrderStatus>>

    fun getById(orderStatusId: Int): Result<OrderStatus>

    fun getByName(orderStatusName: String): Result<OrderStatus>

    fun getByKey(key: String): Result<OrderStatus>

    fun save(entity: OrderStatus): Result<EntityId>
}
