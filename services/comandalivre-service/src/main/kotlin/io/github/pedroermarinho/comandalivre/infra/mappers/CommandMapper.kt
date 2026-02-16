package io.github.pedroermarinho.comandalivre.infra.mappers

import comandalivre.tables.records.CommandStatusRecord
import comandalivre.tables.records.CommandsRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.table.TableDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.CommandEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.command.CommandResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.command.CommandSummaryResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandName
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandPeople
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeId
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyMapper
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeMapper
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.MonetaryValue
import io.github.pedroermarinho.shared.valueobject.UserId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class CommandPersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val commandStatusPersistenceMapper: CommandStatusPersistenceMapper,
) {
    fun toRecord(entity: CommandEntity): Result<CommandsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrNull()
            CommandsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                commandName = entity.name.value,
                numberOfPeople = entity.numberOfPeople.value,
                totalAmount = entity.totalAmount?.value,
                employeeId = entity.employeeId.value,
                statusId = entity.status.id.internalId,
                tableId = entity.tableId.value,
                userId = entity.userId?.value,
                companyId = entity.companyId.value,
                cancellationReason = entity.cancellationReason,
                cancelledByUserId = entity.cancelledByUserId?.value,
                discountAmount = entity.discountAmount?.value,
                discountDescription = entity.discountDescription,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth?.sub,
                updatedBy = userAuth?.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        commandsRecord: CommandsRecord,
        commandStatusRecord: CommandStatusRecord,
    ): Result<CommandEntity> =
        errorDataConversion {
            CommandEntity(
                id =
                    EntityId(
                        internalId = commandsRecord.id!!,
                        publicId = commandsRecord.publicId,
                    ),
                name = CommandName.restore(commandsRecord.commandName),
                numberOfPeople = CommandPeople.restore(commandsRecord.numberOfPeople!!),
                totalAmount = commandsRecord.totalAmount?.let { MonetaryValue.restore(it) },
                employeeId = EmployeeId.restore(commandsRecord.employeeId!!),
                status = commandStatusPersistenceMapper.toEntity(commandStatusRecord).getOrThrow(),
                tableId = TableId.restore(commandsRecord.tableId),
                userId = commandsRecord.userId?.let { UserId.restore(it) },
                companyId = CompanyId.restore(commandsRecord.companyId),
                cancellationReason = commandsRecord.cancellationReason,
                cancelledByUserId = commandsRecord.cancelledByUserId?.let { UserId.restore(it) },
                discountAmount = commandsRecord.discountAmount?.let { MonetaryValue.restore(it) },
                discountDescription = commandsRecord.discountDescription,
                audit =
                    EntityAudit(
                        createdAt = commandsRecord.createdAt!!,
                        updatedAt = commandsRecord.updatedAt!!,
                        deletedAt = commandsRecord.deletedAt,
                        createdBy = commandsRecord.createdBy,
                        updatedBy = commandsRecord.updatedBy,
                        version = commandsRecord.version!!,
                    ),
            )
        }
}

@Component
class CommandMapper(
    private val companyMapper: CompanyMapper,
    private val employeeMapper: EmployeeMapper,
    private val commandStatusMapper: CommandStatusMapper,
    private val tableMapper: TableMapper,
) {
    fun toDTO(
        entity: CommandEntity,
        employee: EmployeeDTO,
        table: TableDTO,
    ) = CommandDTO(
        id = entity.id,
        name = entity.name.value,
        numberOfPeople = entity.numberOfPeople.value,
        totalAmount = entity.totalAmount?.value,
        employee = employee,
        status = commandStatusMapper.toDTO(entity.status),
        table = table,
        cancellationReason = entity.cancellationReason,
        cancelledByUserId = entity.cancelledByUserId?.value,
        discountAmount = entity.discountAmount?.value,
        discountDescription = entity.discountDescription,
        createdAt = entity.audit.createdAt,
    )

    fun toResponse(dto: CommandDTO) =
        CommandResponse(
            id = dto.id.publicId,
            name = dto.name,
            numberOfPeople = dto.numberOfPeople,
            totalAmount = dto.totalAmount,
            employee = employeeMapper.toSummaryResponseDTO(dto.employee),
            status = commandStatusMapper.toResponse(dto.status),
            table = tableMapper.toResponse(dto.table),
            cancellationReason = dto.cancellationReason,
            discountAmount = dto.discountAmount,
            discountDescription = dto.discountDescription,
            createdAt = dto.createdAt,
        )

    fun toSummaryResponse(dto: CommandDTO) =
        CommandSummaryResponse(
            id = dto.id.publicId,
            name = dto.name,
            numberOfPeople = dto.numberOfPeople,
            totalAmount = dto.totalAmount,
            status = commandStatusMapper.toResponse(dto.status),
            table = tableMapper.toResponse(dto.table),
            cancellationReason = dto.cancellationReason,
            discountAmount = dto.discountAmount,
            discountDescription = dto.discountDescription,
            createdAt = dto.createdAt,
        )
}
