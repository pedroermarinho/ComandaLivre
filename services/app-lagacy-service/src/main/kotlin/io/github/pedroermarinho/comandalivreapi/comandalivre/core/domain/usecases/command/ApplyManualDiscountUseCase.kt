package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Transactional
@UseCase
class ApplyManualDiscountUseCase(
    private val commandRepository: CommandRepository,
) {
    // Todo: Implementar lógica de aplicação de desconto manual
    fun execute(
        commandId: UUID,
        discountAmount: BigDecimal,
        discountDescription: String?,
    ) {
        // Lógica para aplicar desconto manual
        // 1. Validar permissões do usuário
        // 2. Atualizar a comanda com o valor do desconto e descrição
        // 3. Recalcular o total da comanda
        // 4. Notificar dashboard

        // Mock de retorno
        println("Desconto de $discountAmount aplicado à comanda $commandId. Motivo: $discountDescription")
    }
}
