package io.github.pedroermarinho.comandalivreapi.util.factory

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanyEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanySettingsEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.CompanyTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.company.CompanySettingsForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.company.CompanyCreateRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.Cnpj
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyName
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.address.AddressDTO
import io.github.pedroermarinho.shared.valueobject.*
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

object MockCompanyFactory {
    fun buildCompanyDTO(): CompanyDTO =
        CompanyDTO(
            id = EntityId(MockConstants.COMPANY_ID_INT, MockConstants.COMPANY_ID_UUID),
            name = MockConstants.COMPANY_NAME,
            email = MockConstants.COMPANY_EMAIL,
            phone = MockConstants.COMPANY_PHONE_NUMBER,
            cnpj = MockConstants.COMPANY_CNPJ,
            description = MockConstants.COMPANY_DESCRIPTION,
            type = buildCompanyTypeDTO(),
            address = buildAddressDTO(),
            settings = null,
            isPublic = true,
            createdAt = LocalDateTime.now(),
        )

    fun buildCompanyTypeDTO(): CompanyTypeDTO =
        CompanyTypeDTO(
            id = EntityId(MockConstants.COMPANY_TYPE_ID_INT, UUID.randomUUID()),
            key = "RESTAURANT",
            name = "Restaurante",
        )

    fun buildAddressDTO(): AddressDTO =
        AddressDTO(
            id = EntityId(1, UUID.randomUUID()),
            street = "Test Street",
            number = "123",
            zipCode = "12345-678",
            city = "Test City",
            state = "TS",
            neighborhood = "Test Neighborhood",
            complement = null,
            createdAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now(),
        )

    fun buildCompanyCreateRequest(): CompanyCreateRequest =
        CompanyCreateRequest(
            name = MockConstants.COMPANY_NAME,
            email = MockConstants.COMPANY_EMAIL,
            phone = MockConstants.COMPANY_PHONE_NUMBER,
            cnpj = MockConstants.COMPANY_CNPJ,
            description = MockConstants.COMPANY_DESCRIPTION,
            isPublic = true,
            type = CompanyTypeEnum.RESTAURANT,
        )

    fun buildCompanyType(
        id: Int = MockConstants.COMPANY_TYPE_ID_INT,
        uuid: UUID = UUID.randomUUID(),
        name: String = "Restaurante",
        key: String = CompanyTypeEnum.RESTAURANT.value,
    ): CompanyType =
        CompanyType(
            id = EntityId(id, uuid),
            name = TypeName(name),
            key = TypeKey.restore(key),
            audit = EntityAudit.createNew(),
        )

    fun buildCompanySettingsForm(): CompanySettingsForm =
        CompanySettingsForm(
            primaryThemeColor = "#FFFFFF",
            secondaryThemeColor = "#000000",
            welcomeMessage = "Welcome!",
            timezone = "America/Sao_Paulo",
            openTime = LocalTime.parse("08:00"),
            closeTime = LocalTime.parse("18:00"),
            domain = "example",
        )

    fun buildCompanyEntity(): CompanyEntity =
        CompanyEntity(
            id = EntityId(MockConstants.COMPANY_ID_INT, MockConstants.COMPANY_ID_UUID),
            name = CompanyName(MockConstants.COMPANY_NAME),
            email = EmailAddress(MockConstants.COMPANY_EMAIL),
            phone = PhoneNumber(MockConstants.COMPANY_PHONE_NUMBER),
            cnpj = Cnpj(MockConstants.COMPANY_CNPJ),
            description = MockConstants.COMPANY_DESCRIPTION,
            companyType = buildCompanyType(),
            addressId = null,
            settings = CompanySettingsEntity.createNew(),
            isPublic = true,
            audit = EntityAudit.createNew(),
        )
}
