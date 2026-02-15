package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.event.TableStatusEvent
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.ChangeStatusOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.SearchOrderUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class ChangeStatusCommandUseCase(
    private val commandRepository: CommandRepository,
    private val searchCommandStatusUseCase: SearchCommandStatusUseCase,
    private val searchCommandUseCase: SearchCommandUseCase,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val searchOrderUseCase: SearchOrderUseCase,
    private val changeStatusOrderUseCase: ChangeStatusOrderUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun execute(
        commandId: UUID,
        statusEnum: CommandStatusEnum,
        closeAll: Boolean = false,
    ): Result<Unit> =
        runCatching {
            log.info { "Iniciando mudaça de status da comanda $commandId para $statusEnum, fechar orders abertas: $closeAll" }
            var command = searchCommandUseCase.getEntityById(commandId).getOrThrow()

            if (!CommandStatusEnum.from(command.status).canTransitionTo(statusEnum)) {
                log.error { "Transição de status inválida: de '${command.status.key}' para '${statusEnum.value}'" }
                throw BusinessLogicException("Transição de status de '${command.status.key.value}' para '${statusEnum.value}' não é permitida.")
            }

            val status = searchCommandStatusUseCase.getByEnum(statusEnum).getOrThrow()

            if (statusEnum == CommandStatusEnum.CLOSED && !closeAll) {
                log.info { "Verificando se a comanda ${command.id.internalId} pode ser fechada" }
                checkIfCanBeClosed(command.id.internalId).getOrThrow()
            }

            if (statusEnum == CommandStatusEnum.CLOSED && closeAll) {
                changeStatusOrderUseCase.closeAll(command.id.internalId).getOrThrow()
            }

            if (statusEnum == CommandStatusEnum.CANCELED) {
                command =
                    command.updateCancelInfo(
                        cancellationReason = "Razão de cancelamento não informada",
                        cancelledByUserId = currentUserUseCase.getUserId().getOrThrow(),
                    )
            }

            log.info { "Status de comanda ${command.id.internalId} atualizando para de '${command.status.key}' para '${statusEnum.value}'" }
            commandRepository.save(command.updateStatus(status)).getOrThrow()
            log.info { "Status de comanda $commandId atualizado para '${statusEnum.value}'" }

            applicationEventPublisher.publishEvent(
                TableStatusEvent(tableId = command.tableId.value),
            )
            return Result.success(Unit)
        }

    private fun checkIfCanBeClosed(commandId: Int): Result<Unit> =
        runCatching {
            val isFullyClosed = searchOrderUseCase.isCommandFullyClosed(commandId).getOrThrow()
            log.info { "As ordens da comanda $commandId está totalmente fechada: $isFullyClosed" }
            if (!isFullyClosed) {
                log.error { "Não é possível fechar a comanda $commandId porque há pedidos em aberto." }
                throw BusinessLogicException("Não é possível fechar a comanda porque há pedidos em aberto.")
            }
            return Result.success(Unit)
        }
}
