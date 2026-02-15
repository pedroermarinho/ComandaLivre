package io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers

import company.tables.records.CompanySettingsRecord
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanySettingsDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanySettingsEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanySettingsResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.DomainName
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.asset.GetUrlAssetUseCase
import io.github.pedroermarinho.shared.valueobject.AssetId
import io.github.pedroermarinho.shared.valueobject.EmailAddress
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class CompanySettingsPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: CompanySettingsEntity): Result<CompanySettingsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            CompanySettingsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                companyId = entity.companyId.value,
                logoAssetId = entity.logoAssetId?.value,
                bannerAssetId = entity.bannerAssetId?.value,
                primaryThemeColor = entity.primaryThemeColor,
                secondaryThemeColor = entity.secondaryThemeColor,
                welcomeMessage = entity.welcomeMessage,
                timezone = entity.timezone,
                openTime = entity.openTime,
                closeTime = entity.closeTime,
                isClosed = entity.isClosed,
                notificationEmails = entity.notificationEmails?.map { it.value }?.toTypedArray(),
                domain = entity.domain?.value,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: CompanySettingsRecord): Result<CompanySettingsEntity> =
        errorDataConversion {
            CompanySettingsEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                companyId = CompanyId.restore(record.companyId),
                logoAssetId = record.logoAssetId?.let { AssetId.restore(it) },
                bannerAssetId = record.bannerAssetId?.let { AssetId.restore(it) },
                primaryThemeColor = record.primaryThemeColor,
                secondaryThemeColor = record.secondaryThemeColor,
                welcomeMessage = record.welcomeMessage,
                domain = record.domain?.let { DomainName.restore(it) },
                timezone = record.timezone,
                openTime = record.openTime,
                closeTime = record.closeTime,
                isClosed = record.isClosed,
                notificationEmails = record.notificationEmails?.filterNotNull()?.map { EmailAddress.restore(it) },
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
class CompanySettingsMapper(
    private val getUrlAssetUseCase: GetUrlAssetUseCase,
) {
    fun toDTO(entity: CompanySettingsEntity) =
        runCatching {
            val logo = entity.logoAssetId?.let { getUrlAssetUseCase.execute(it.value).getOrNull() }
            val banner = entity.bannerAssetId?.let { getUrlAssetUseCase.execute(it.value).getOrNull() }
            CompanySettingsDTO(
                id = entity.id,
                logo = logo,
                banner = banner,
                primaryThemeColor = entity.primaryThemeColor,
                secondaryThemeColor = entity.secondaryThemeColor,
                welcomeMessage = entity.welcomeMessage,
                domain = entity.domain?.value,
                timezone = entity.timezone,
                openTime = entity.openTime,
                closeTime = entity.closeTime,
                isClosed = entity.isClosed,
                notificationEmails = entity.notificationEmails?.map { it.value },
            )
        }

    fun toResponse(dto: CompanySettingsDTO) =
        CompanySettingsResponse(
            id = dto.id.publicId,
            logo = dto.logo,
            banner = dto.banner,
            primaryThemeColor = dto.primaryThemeColor,
            secondaryThemeColor = dto.secondaryThemeColor,
            welcomeMessage = dto.welcomeMessage,
            domain = dto.domain,
            openTime = dto.openTime,
            closeTime = dto.closeTime,
            isClosed = dto.isClosed,
        )
}
