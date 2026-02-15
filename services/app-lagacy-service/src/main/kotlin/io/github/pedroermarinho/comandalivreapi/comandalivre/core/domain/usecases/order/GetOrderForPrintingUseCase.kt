package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderForPrintingDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@UseCase
class GetOrderForPrintingUseCase(
    private val orderRepository: OrderRepository,
) {
    // TODO: Implementar a lógica real para buscar e formatar os dados do pedido
    fun execute(orderId: UUID): OrderForPrintingDTO {
        // TODO: implementação mock
        // Lógica para buscar os dados do pedido e formatar para impressão
        // Por simplicidade, mockando os dados aqui
        return OrderForPrintingDTO(
            publicId = orderId,
            productName = "Produto Mock",
            quantity = 1,
            notes = "Sem cebola",
        )
    }
}
