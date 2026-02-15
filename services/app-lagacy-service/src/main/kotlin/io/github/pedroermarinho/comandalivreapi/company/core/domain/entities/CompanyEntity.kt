package io.github.pedroermarinho.comandalivreapi.company.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.Cnpj
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyName
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.DomainName
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.*
import java.time.LocalTime
import java.util.*

data class CompanyEntity(
    val id: EntityId,
    val name: CompanyName,
    val email: EmailAddress?,
    val phone: PhoneNumber?,
    val cnpj: Cnpj?,
    val description: String?,
    val companyType: CompanyType,
    val addressId: AddressId?,
    val settings: CompanySettingsEntity,
    val isPublic: Boolean,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            name: String,
            companyType: CompanyType,
            email: String?,
            phone: String?,
            cnpj: String?,
            description: String?,
            addressId: Int? = null,
            isPublic: Boolean = false,
        ): CompanyEntity =
            CompanyEntity(
                id = EntityId.createNew(publicId = publicId),
                name = CompanyName(name),
                email = email?.let { EmailAddress(it) },
                phone = phone?.let { PhoneNumber(it) },
                cnpj = cnpj?.let { Cnpj(it) },
                description = description,
                companyType = companyType,
                addressId = addressId?.let { AddressId(it) },
                settings = CompanySettingsEntity.createNew(),
                isPublic = isPublic,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        name: String,
        email: String?,
        phone: String?,
        cnpj: String?,
        description: String?,
        isPublic: Boolean,
    ): CompanyEntity =
        this.copy(
            name = CompanyName(name),
            email = email?.let { EmailAddress(it) },
            phone = phone?.let { PhoneNumber(it) },
            cnpj = cnpj?.let { Cnpj(it) },
            description = description,
            isPublic = isPublic,
            audit = this.audit.update(),
        )

    fun updateCompanyType(companyType: CompanyType): CompanyEntity =
        this.copy(
            companyType = companyType,
            audit = this.audit.update(),
        )

    fun updateAddress(addressId: Int): CompanyEntity =
        this.copy(
            addressId = AddressId(addressId),
            audit = this.audit.update(),
        )

    fun updateSettings(settings: CompanySettingsEntity): CompanyEntity =
        this.copy(
            settings =
                settings.copy(
                    audit = settings.audit.update(),
                ),
            audit = this.audit.update(),
        )

    fun updateSettingsInfor(
        primaryThemeColor: String?,
        secondaryThemeColor: String?,
        welcomeMessage: String?,
        timezone: String?,
        openTime: LocalTime?,
        closeTime: LocalTime?,
        domain: String?,
    ): CompanyEntity =
        this.copy(
            settings =
                settings.copy(
                    primaryThemeColor = primaryThemeColor,
                    secondaryThemeColor = secondaryThemeColor,
                    welcomeMessage = welcomeMessage,
                    timezone = timezone,
                    openTime = openTime,
                    closeTime = closeTime,
                    domain = domain?.let { DomainName(it) },
                    audit = settings.audit.update(),
                ),
            audit = this.audit.update(),
        )

    fun updateLogo(assetId: Int): CompanyEntity =
        this.copy(
            settings =
                settings.copy(
                    logoAssetId = AssetId(assetId),
                    audit = settings.audit.update(),
                ),
            audit = this.audit.update(),
        )

    fun updateBanner(assetId: Int): CompanyEntity =
        this.copy(
            settings =
                settings.copy(
                    bannerAssetId = AssetId(assetId),
                    audit = settings.audit.update(),
                ),
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
