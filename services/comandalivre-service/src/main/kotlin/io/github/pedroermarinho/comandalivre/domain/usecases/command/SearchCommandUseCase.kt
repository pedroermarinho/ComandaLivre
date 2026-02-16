package io.github.pedroermarinho.comandalivre.domain.usecases.command

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandFilterDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.CommandEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.SearchTableUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableId
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.CommandMapper
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchCommandUseCase(
    private val commandRepository: CommandRepository,
    private val searchCommandStatusUseCase: SearchCommandStatusUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchTableUseCase: SearchTableUseCase,
    private val commandMapper: CommandMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getById(commandId: UUID): Result<CommandDTO> = runCatching { commandRepository.getById(commandId).map { convert(it).getOrThrow() }.getOrThrow() }

    fun getEntityById(commandId: UUID): Result<CommandEntity> = runCatching { commandRepository.getById(commandId).getOrThrow() }

    fun getEntityById(commandId: Int): Result<CommandEntity> = runCatching { commandRepository.getById(commandId).getOrThrow() }

    fun getById(id: Int): Result<CommandDTO> = runCatching { commandRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow() }

    fun getAll(
        pageable: PageableDTO,
        filter: CommandFilterDTO,
    ): Result<PageDTO<CommandDTO>> =
        runCatching {
            if (filter.tableId == null && filter.companyId == null) {
                throw BusinessLogicException("É necessário informar o ID da mesa ou o ID do restaurante para buscar as comandas.")
            }

            commandRepository.getAll(pageable, filter).map { page -> page.map { convert(it).getOrThrow() } }.getOrThrow()
        }

    fun getAllList(
        companyId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        status: CommandStatusEnum,
    ): Result<List<CommandDTO>> =
        runCatching {
            val statusId =
                searchCommandStatusUseCase
                    .getByEnum(status)
                    .getOrThrow()
                    .id.internalId
            commandRepository
                .getAllList(
                    companyId = companyId,
                    startDate = startDate,
                    endDate = endDate,
                    statusId = statusId,
                ).map { it.map { item -> convert(item).getOrThrow() } }
                .getOrThrow()
        }

    fun getIdById(id: UUID): Result<EntityId> = commandRepository.getIdById(id)

    fun count(): Result<Long> = commandRepository.count()

    fun existsByTableIdAndStatusIn(
        tableId: Int,
        statusKeys: List<CommandStatusEnum>,
    ): Boolean = commandRepository.existsByTableIdAndStatusIn(TableId(tableId), statusKeys)

    fun exists(id: UUID): Boolean = commandRepository.exists(id)

    fun checkExists(id: UUID): Result<Unit> {
        if (exists(id)) {
            return Result.success(Unit)
        }

        log.error { "Comanda não encontrada para o ID: $id" }
        return Result.failure(NotFoundException("Comanda não encontrada"))
    }

    private fun convert(entity: CommandEntity): Result<CommandDTO> =
        runCatching {
            commandMapper.toDTO(
                entity = entity,
                employee = searchEmployeeUseCase.getById(entity.employeeId.value).getOrThrow(),
                table = searchTableUseCase.getByIdUnsafe(entity.tableId.value).getOrThrow(),
            )
        }.onFailure { log.error(it) { "Erro ao converter TableEntity para TableDTO para o ID da entidade: ${entity.id}" } }
}
