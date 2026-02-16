package io.github.pedroermarinho.comandalivre.domain.usecases.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderRepository
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@UseCase
class MergeCommandsUseCase(
    private val commandRepository: CommandRepository,
    private val orderRepository: OrderRepository,
) {
    // TODO: Implementar lógica de junção de comandas
    fun execute(
        sourceCommandIds: List<UUID>,
        targetCommandId: UUID,
    ) {
        // Lógica para juntar comandas
        // 1. Validar se as comandas existem e estão ativas
        // 2. Para cada comanda de origem:
        //    a. Mover todos os seus itens de pedido para a comanda de destino
        //    b. Marcar a comanda de origem como 'merged' ou 'cancelled' com motivo
        // 3. Recalcular o total da comanda de destino
        // 4. Notificar dashboard sobre mudanças nas comandas

        // Mock de retorno
        println("Comandas $sourceCommandIds unidas na comanda $targetCommandId")
    }
}
