package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.TableStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.SearchCommandUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateTableStatusUseCase(
    private val searchCommandUseCase: SearchCommandUseCase,
    private val tableRepository: TableRepository,
    private val searchTableUseCase: SearchTableUseCase,
    private val searchTableStatusUseCase: SearchTableStatusUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun execute(tableId: Int): Result<Unit> =
        runCatching {
            val table = searchTableUseCase.getById(tableId).getOrThrow()
            val activeStatuses =
                listOf(
                    CommandStatusEnum.OPEN,
                    CommandStatusEnum.PAYING,
                    CommandStatusEnum.PARTIALLY_PAID,
                )
            val hasActiveCommand = searchCommandUseCase.existsByTableIdAndStatusIn(table.id.internalId, activeStatuses)

            log.info { "Verificando se a mesa ${table.name}:${table.id.publicId} possui comandas ativas: $hasActiveCommand" }

            val newStatus = if (hasActiveCommand) TableStatusEnum.OCCUPIED else TableStatusEnum.AVAILABLE

            this.execute(table.id.publicId, newStatus).getOrThrow()
            log.info { "Status da mesa ${table.name}:${table.id.publicId} atualizado para ${newStatus.value}" }
        }

    fun execute(
        publicId: UUID,
        status: TableStatusEnum,
    ): Result<Unit> =
        runCatching {
            log.info { "Atualizando status da mesa com ID: $publicId para ${status.value}" }
            val status = searchTableStatusUseCase.getByEnum(status).getOrThrow()
            val table = tableRepository.getById(publicId).getOrThrow()
            tableRepository.save(table.updateStatus(status)).getOrThrow()
        }
}
