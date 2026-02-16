package io.github.pedroermarinho.comandalivre.domain.usecases.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderRepository
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@UseCase
class TransferOrderItemsUseCase(
    private val orderRepository: OrderRepository,
    private val commandRepository: CommandRepository,
) {
    // todo: Implementar lógica de transferência de itens de pedido entre comandas
    fun execute(
        itemsToMove: List<UUID>,
        fromCommandId: UUID,
        toCommandId: UUID,
    ) {
        // Lógica para transferir itens de pedido entre comandas
        // 1. Validar se as comandas existem e estão ativas
        // 2. Para cada item em itemsToMove:
        //    a. Buscar o item de pedido
        //    b. Remover o item da comanda de origem (marcar como cancelado ou transferido)
        //    c. Criar um novo item de pedido na comanda de destino com os mesmos detalhes
        // 3. Recalcular totais das comandas envolvidas
        // 4. Notificar dashboard sobre mudanças nas comandas

        // Mock de retorno
        println("Itens $itemsToMove transferidos da comanda $fromCommandId para $toCommandId")
    }
}
