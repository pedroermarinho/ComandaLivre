package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.OrderEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.OrderStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.event.CommandEvent
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderItemModifierRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductModifierRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.order.OrderCreateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.SearchCommandUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product.SearchProductUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.CheckPermissionCompanyUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class AddOrderUseCase(
    private val orderRepository: OrderRepository,
    private val searchCommandUseCase: SearchCommandUseCase,
    private val searchProductUseCase: SearchProductUseCase,
    private val searchStatusOrderUseCase: SearchStatusOrderUseCase,
    private val checkPermissionCompanyUseCase: CheckPermissionCompanyUseCase,
    private val productModifierRepository: ProductModifierRepository,
    private val orderItemModifierRepository: OrderItemModifierRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val log = KotlinLogging.logger {}

    fun add(form: OrderCreateRequest): Result<Unit> =
        runCatching {
            log.info { "Iniciando adição de pedido: $form" }
            val command = searchCommandUseCase.getById(form.commandId).getOrThrow()
            val status = searchStatusOrderUseCase.getByEnum(OrderStatusEnum.PENDING_CONFIRMATION).getOrThrow()

            checkPermissionCompanyUseCase.execute(command.table.companyId).getOrThrow()

            if (!CommandStatusEnum.OPEN.matches(command.status)) {
                return Result.failure(BusinessLogicException("Não é possível adicionar um pedido para um comando fechado"))
            }

            form.items.map { item ->
                val product = searchProductUseCase.getByIdWithModifiers(item.productId).getOrThrow()

                if (product.company.id.internalId != command.table.companyId) {
                    return Result.failure(
                        BusinessLogicException("Produto ${product.name} não pertence à empresa do comando"),
                    )
                }

                val selectedModifiers = productModifierRepository.getOptionIdsByPublicIds(item.selectedModifierOptionIds).getOrThrow()
                val modifierOptions = product.modifierGroups.flatMap { it.options }

                product.modifierGroups.forEach { group ->
                    val selectedOptionsInGroup =
                        group.options
                            .filter { selectedModifiers.map { sm -> sm.publicId }.contains(it.id.publicId) }

                    val selectedCount = selectedOptionsInGroup.count()

                    if (selectedCount < group.minSelection) {
                        return Result.failure(BusinessLogicException("O grupo '${group.name}' requer no mínimo ${group.minSelection} seleções."))
                    }

                    if (selectedCount > group.maxSelection) {
                        return Result.failure(BusinessLogicException("O grupo '${group.name}' permite no máximo ${group.maxSelection} seleções."))
                    }
                }

                val totalModifiersPrice =
                    modifierOptions
                        .filter { selectedModifiers.map { sm -> sm.publicId }.contains(it.id.publicId) }
                        .sumOf { it.priceChange }

                val orderResult =
                    orderRepository
                        .save(
                            OrderEntity.createNew(
                                commandId = command.id.internalId,
                                productId = product.id.internalId,
                                status = status,
                                basePriceAtOrder = product.price,
                                totalModifiersPriceAtOrder = totalModifiersPrice,
                                notes = item.notes,
                            ),
                        ).getOrThrow()

                selectedModifiers.forEach { modifier ->
                    orderItemModifierRepository.create(orderResult.internalId, modifier.internalId).getOrThrow()
                }

                log.info { "Pedido adicionado com sucesso: $orderResult" }
            }

            applicationEventPublisher.publishEvent(
                CommandEvent(commandId = command.id.internalId),
            )

            return Result.success(Unit)
        }
}
