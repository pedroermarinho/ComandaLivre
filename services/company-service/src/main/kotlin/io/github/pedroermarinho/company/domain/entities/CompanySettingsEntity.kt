package io.github.pedroermarinho.company.domain.entities

import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.DomainName
import io.github.pedroermarinho.shared.valueobject.AssetId
import io.github.pedroermarinho.shared.valueobject.EmailAddress
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalTime
import java.util.*

data class CompanySettingsEntity(
    val id: EntityId,
    val companyId: CompanyId,
    val logoAssetId: AssetId?,
    val bannerAssetId: AssetId?,
    val primaryThemeColor: String?,
    val secondaryThemeColor: String?,
    val welcomeMessage: String?,
    val timezone: String?,
    val openTime: LocalTime?,
    val closeTime: LocalTime?,
    val isClosed: Boolean?,
    val notificationEmails: List<EmailAddress>?,
    val domain: DomainName?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            companyId: CompanyId? = null,
            logoAssetId: Int? = null,
            bannerAssetId: Int? = null,
            primaryThemeColor: String? = null,
            secondaryThemeColor: String? = null,
            welcomeMessage: String? = null,
            timezone: String? = null,
            openTime: LocalTime? = null,
            closeTime: LocalTime? = null,
            isClosed: Boolean? = null,
            notificationEmails: List<String>? = null,
            domain: String? = null,
        ): CompanySettingsEntity =
            CompanySettingsEntity(
                id = EntityId.createNew(publicId = publicId),
                companyId = companyId ?: CompanyId.invalid(),
                logoAssetId = logoAssetId?.let { AssetId(it) },
                bannerAssetId = bannerAssetId?.let { AssetId(it) },
                primaryThemeColor = primaryThemeColor,
                secondaryThemeColor = secondaryThemeColor,
                welcomeMessage = welcomeMessage,
                timezone = timezone,
                openTime = openTime,
                closeTime = closeTime,
                isClosed = isClosed,
                notificationEmails = notificationEmails?.map { EmailAddress(it) },
                domain = domain?.let { DomainName(it) },
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun updateCompany(companyId: Int): CompanySettingsEntity =
        this.copy(
            companyId = CompanyId(companyId),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
