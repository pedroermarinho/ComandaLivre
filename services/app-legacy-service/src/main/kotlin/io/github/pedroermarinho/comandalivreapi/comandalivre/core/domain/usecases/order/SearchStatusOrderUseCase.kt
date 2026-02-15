package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderStatusDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.OrderStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderStatusRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.OrderStatusMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class SearchStatusOrderUseCase(
    private val orderStatusRepository: OrderStatusRepository,
    private val orderStatusMapper: OrderStatusMapper,
) {
    fun getAll(pageable: PageableDTO): Result<PageDTO<OrderStatusDTO>> = orderStatusRepository.getAll(pageable).map { it.map { entity -> orderStatusMapper.toDTO(entity) } }

    fun getAll(): Result<List<OrderStatusDTO>> = orderStatusRepository.getAll().map { it.map { entity -> orderStatusMapper.toDTO(entity) } }

    fun getById(orderStatusId: Int): Result<OrderStatusDTO> = orderStatusRepository.getById(orderStatusId).map { orderStatusMapper.toDTO(it) }

    fun getByName(statusName: String): Result<OrderStatusDTO> = orderStatusRepository.getByName(statusName).map { orderStatusMapper.toDTO(it) }

    fun getByKey(key: String): Result<OrderStatus> = orderStatusRepository.getByKey(key)

    fun getByEnum(orderStatus: OrderStatusEnum): Result<OrderStatus> = getByKey(orderStatus.value)
}
