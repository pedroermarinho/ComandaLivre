package io.github.pedroermarinho.comandalivre.domain.usecases.order

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderFilterDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.OrderEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.OrderStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.SearchCommandUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product.SearchProductUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.OrderMapper
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchOrderUseCase(
    private val orderRepository: OrderRepository,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchProductUseCase: SearchProductUseCase,
    private val searchStatusOrderUseCase: SearchStatusOrderUseCase,
    private val searchCommandUseCase: SearchCommandUseCase,
    private val orderMapper: OrderMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getAll(
        pageable: PageableDTO,
        filter: OrderFilterDTO,
    ): Result<PageDTO<OrderDTO>> =
        runCatching {
            filter.companyPublicId?.let { searchCompanyUseCase.checkExists(it).getOrThrow() }
            filter.commandPublicId?.let { searchCommandUseCase.checkExists(it).getOrThrow() }
            val companyId: Int? = filter.companyPublicId?.let { searchCompanyUseCase.getIdById(it).getOrThrow() }
            val newFilter = filter.copy(companyId = companyId)
            newFilter.valid().getOrThrow()
            orderRepository.getAll(pageable, newFilter).map { page -> page.map { convert(it).getOrThrow() } }.getOrThrow()
        }

    fun getAllList(commandId: Int): Result<List<OrderDTO>> = runCatching { orderRepository.getAll(commandId).map { it.map { convert(it).getOrThrow() } }.getOrThrow() }

    fun getEntityAllList(commandId: Int): Result<List<OrderEntity>> = orderRepository.getAll(commandId)

    fun getById(orderId: Int): Result<OrderDTO> = runCatching { orderRepository.getById(orderId).map { convert(it).getOrThrow() }.getOrThrow() }

    fun getById(publicId: UUID): Result<OrderDTO> = runCatching { orderRepository.getById(publicId).map { convert(it).getOrThrow() }.getOrThrow() }

    fun getEntityById(publicId: UUID): Result<OrderEntity> = orderRepository.getById(publicId)

    fun isCommandFullyClosed(commandId: Int): Result<Boolean> =
        runCatching {
            val orders = orderRepository.getAll(commandId).getOrThrow()
            val invalidValue = listOf(OrderStatusEnum.PENDING_CONFIRMATION, OrderStatusEnum.IN_PREPARATION, OrderStatusEnum.READY_FOR_DELIVERY)
            orders.none { order ->
                val statusEnum = OrderStatusEnum.fromValue(order.status.key.value).getOrThrow()
                invalidValue.contains(statusEnum)
            }
        }

    fun isCommandFullyClosed(commandId: UUID): Result<Boolean> =
        runCatching {
            val commandId = searchCommandUseCase.getIdById(commandId).getOrThrow()
            isCommandFullyClosed(commandId.internalId).getOrThrow()
        }

    private fun convert(entity: OrderEntity): Result<OrderDTO> =
        runCatching {
            orderMapper.toDTO(
                entity = entity,
                command = searchCommandUseCase.getById(entity.commandId.value).getOrThrow(),
                product = searchProductUseCase.getById(entity.productId.value).getOrThrow(),
            )
        }.onFailure { log.error(it) { "Erro ao converter TableEntity para TableDTO para o ID da entidade: ${entity.id}" } }
}
