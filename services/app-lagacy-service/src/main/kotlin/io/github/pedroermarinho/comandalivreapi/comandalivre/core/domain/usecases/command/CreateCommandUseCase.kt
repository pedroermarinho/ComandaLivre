package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.CommandEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.event.TableStatusEvent
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.command.CommandRequestForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.SearchTableUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateCommandUseCase(
    private val commandRepository: CommandRepository,
    private val searchCommandStatusUseCase: SearchCommandStatusUseCase,
    private val searchTableUseCase: SearchTableUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val log = KotlinLogging.logger {}

    fun create(form: CommandRequestForm): Result<EntityId> =
        runCatching {
            val table = searchTableUseCase.getById(form.tableId).getOrThrow()
            val employee = searchEmployeeUseCase.getById(form.employeeId).getOrThrow()
            val status = searchCommandStatusUseCase.getByEnum(CommandStatusEnum.OPEN).getOrThrow()
            val user = form.userId?.let { searchUserUseCase.getById(it).getOrThrow() }

            val result =
                commandRepository
                    .save(
                        CommandEntity.createNew(
                            publicId = form.publicId,
                            name = form.name,
                            numberOfPeople = form.numberOfPeople,
                            employeeId = employee.id.internalId,
                            status = status,
                            tableId = table.id.internalId,
                            userId = user?.id?.internalId,
                            companyId = table.companyId,
                        ),
                    ).getOrThrow()

            log.info { "Comanda criado com sucesso - ID: ${result.internalId}, Restaurante: ${table.companyId}" }
            applicationEventPublisher.publishEvent(
                TableStatusEvent(tableId = table.id.internalId),
            )
            result
        }
}
