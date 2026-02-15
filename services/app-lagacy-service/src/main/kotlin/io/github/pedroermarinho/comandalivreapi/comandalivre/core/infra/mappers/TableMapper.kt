package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers

import comandalivre.tables.records.TableStatusRecord
import comandalivre.tables.records.TablesRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.table.TableDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.TableEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.table.TableResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableCapacity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableName
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class TablePersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val tableStatusPersistenceMapper: TableStatusPersistenceMapper,
) {
    fun toRecord(entity: TableEntity): Result<TablesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrNull()
            TablesRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                name = entity.name.value,
                numPeople = entity.numPeople.value,
                statusId = entity.status.id.internalId,
                description = entity.description,
                companyId = entity.companyId.value,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth?.sub,
                updatedBy = userAuth?.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        tablesRecord: TablesRecord,
        tableStatusRecord: TableStatusRecord,
    ): Result<TableEntity> =
        errorDataConversion {
            TableEntity(
                id =
                    EntityId(
                        internalId = tablesRecord.id!!,
                        publicId = tablesRecord.publicId,
                    ),
                name = TableName.restore(tablesRecord.name),
                numPeople = TableCapacity.restore(tablesRecord.numPeople!!),
                status = tableStatusPersistenceMapper.toEntity(tableStatusRecord).getOrThrow(),
                description = tablesRecord.description,
                companyId = CompanyId.restore(tablesRecord.companyId),
                audit =
                    EntityAudit(
                        createdAt = tablesRecord.createdAt!!,
                        updatedAt = tablesRecord.updatedAt!!,
                        deletedAt = tablesRecord.deletedAt,
                        createdBy = tablesRecord.createdBy,
                        updatedBy = tablesRecord.updatedBy,
                        version = tablesRecord.version!!,
                    ),
            )
        }
}

@Component
class TableMapper(
    private val tableStatusMapper: TableStatusMapper,
) {
    fun toDTO(entity: TableEntity) =
        TableDTO(
            id = entity.id,
            name = entity.name.value,
            numPeople = entity.numPeople.value,
            status = tableStatusMapper.toDTO(entity.status),
            description = entity.description,
            companyId = entity.companyId.value,
            createdAt = entity.audit.createdAt,
            updatedAt = entity.audit.updatedAt,
        )

    fun toResponse(dto: TableDTO) =
        TableResponse(
            id = dto.id.publicId,
            name = dto.name,
            numPeople = dto.numPeople,
            status = tableStatusMapper.toResponse(dto.status),
            description = dto.description,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
        )
}
