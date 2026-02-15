package io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.feature.FeatureDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.group.GroupDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.group.GroupFeaturePermissionDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.GroupFeaturePermissionEntity
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.util.errorDataConversion
import shared.tables.records.GroupFeaturePermissionsRecord

fun GroupFeaturePermissionsRecord.toEntity(): Result<GroupFeaturePermissionEntity> =
    errorDataConversion {
        GroupFeaturePermissionEntity(
            featureGroupId = this.featureGroupId,
            featureId = this.featureId,
            isEnabled = this.isEnabled!!,
            grantedAt = this.grantedAt!!,
            audit =
                EntityAudit(
                    createdAt = this.createdAt!!,
                    updatedAt = this.updatedAt!!,
                    deletedAt = this.deletedAt,
                    createdBy = this.createdBy,
                    updatedBy = this.updatedBy,
                    version = this.version!!,
                ),
        )
    }

fun GroupFeaturePermissionEntity.toDTO(
    featureGroup: GroupDTO,
    feature: FeatureDTO,
): GroupFeaturePermissionDTO =
    GroupFeaturePermissionDTO(
        featureGroup = featureGroup,
        feature = feature,
        isEnabled = this.isEnabled,
        grantedAt = this.grantedAt,
        createdAt = this.audit.createdAt,
    )
