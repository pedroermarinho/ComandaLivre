package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.BillDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.BillItemDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.SearchOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.CommandMapper
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductMapper
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Transactional(readOnly = true)
@UseCase
class GetBillForPrintingUseCase(
    private val searchCommandUseCase: SearchCommandUseCase,
    private val searchOrderUseCase: SearchOrderUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val commandMapper: CommandMapper,
    private val companyMapper: CompanyMapper,
    private val productMapper: ProductMapper,
) {
    fun execute(commandId: UUID): Result<BillDTO> =
        runCatching {
            val command = searchCommandUseCase.getById(commandId).getOrThrow()
            val orders = searchOrderUseCase.getAllList(command.id.internalId).getOrThrow()
            val company = searchCompanyUseCase.getById(command.table.companyId).getOrThrow()

            if (orders.isEmpty()) {
                throw BusinessLogicException("Não há pedidos associados a esta comanda.")
            }

            if (command.totalAmount == null || command.totalAmount <= BigDecimal.ZERO) {
                throw BusinessLogicException("O valor total da comanda é inválido.")
            }

            val billItems = aggregateOrdersToBillItems(orders)

            if (!checkTotalAmount(billItems, command.totalAmount)) {
                throw BusinessLogicException("O valor total da comanda não corresponde à soma dos itens.")
            }

            BillDTO(
                command = commandMapper.toSummaryResponse(command),
                company = companyMapper.toResponse(company),
                items = billItems,
            )
        }

    private fun aggregateOrdersToBillItems(orders: List<OrderDTO>): List<BillItemDTO> =
        orders
            .groupBy { Pair(it.product.id.publicId, it.basePriceAtOrder) }
            .map { (key, items) ->
                val (productId, itemPriceAtOrder) = key
                val totalQuantity = items.size
                val unitPrice = itemPriceAtOrder ?: items.first().product.price
                val totalPrice = unitPrice.multiply(BigDecimal(totalQuantity))

                BillItemDTO(
                    product = productMapper.toResponse(items.first().product),
                    quantity = totalQuantity,
                    unitPrice = unitPrice,
                    totalPrice = totalPrice,
                )
            }

    private fun checkTotalAmount(
        billItems: List<BillItemDTO>,
        expectedTotal: BigDecimal,
    ): Boolean {
        val calculatedTotal = billItems.fold(BigDecimal.ZERO) { acc, item -> acc + item.totalPrice }
        return calculatedTotal == expectedTotal
    }
}
