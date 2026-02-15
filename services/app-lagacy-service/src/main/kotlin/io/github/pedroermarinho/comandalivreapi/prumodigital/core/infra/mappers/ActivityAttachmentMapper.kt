package io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ActivityAttachmentDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyActivityDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ActivityAttachmentEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.activityattachment.ActivityAttachmentResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.asset.AssetDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.AssetMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.errorDataConversion
import org.springframework.stereotype.Component
import prumodigital.tables.records.ActivityAttachmentsRecord

@Component
class ActivityAttachmentPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: ActivityAttachmentEntity): Result<ActivityAttachmentsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            ActivityAttachmentsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                dailyActivityId = entity.dailyActivityId,
                assetId = entity.assetId,
                description = entity.description,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: ActivityAttachmentsRecord): Result<ActivityAttachmentEntity> =
        errorDataConversion {
            ActivityAttachmentEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                dailyActivityId = record.dailyActivityId,
                assetId = record.assetId,
                description = record.description,
                audit =
                    EntityAudit(
                        createdAt = record.createdAt!!,
                        updatedAt = record.updatedAt!!,
                        deletedAt = record.deletedAt,
                        createdBy = record.createdBy,
                        updatedBy = record.updatedBy,
                        version = record.version!!,
                    ),
            )
        }
}

@Component
class ActivityAttachmentMapper(
    private val dailyActivityMapper: DailyActivityMapper,
    private val assetMapper: AssetMapper,
) {
    fun toDTO(
        entity: ActivityAttachmentEntity,
        dailyActivity: DailyActivityDTO,
        asset: AssetDTO,
    ): ActivityAttachmentDTO =
        ActivityAttachmentDTO(
            id = entity.id,
            dailyActivity = dailyActivity,
            asset = asset,
            description = entity.description,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: ActivityAttachmentDTO): ActivityAttachmentResponse =
        ActivityAttachmentResponse(
            id = dto.id.publicId,
            dailyActivity = dailyActivityMapper.toResponse(dto.dailyActivity),
            assetId = dto.asset.id.publicId,
            description = dto.description,
            createdAt = dto.createdAt,
        )
}
