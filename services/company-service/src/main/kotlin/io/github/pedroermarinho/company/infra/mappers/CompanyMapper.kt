package io.github.pedroermarinho.company.infra.mappers

import company.tables.records.CompaniesRecord
import company.tables.records.CompanySettingsRecord
import company.tables.records.CompanyTypesRecord
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanyEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanySettingsEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanyResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanySummaryResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.Cnpj
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyName
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.user.domain.usecases.address.SearchAddressUseCase
import io.github.pedroermarinho.shared.valueobject.*
import io.github.pedroermarinho.user.infra.mappers.AddressMapper
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class CompanyPersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val companySettingsPersistenceMapper: CompanySettingsPersistenceMapper,
    private val companyTypePersistenceMapper: CompanyTypePersistenceMapper,
) {
    private val log = KotlinLogging.logger {}

    fun toRecord(entity: CompanyEntity): Result<CompaniesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            CompaniesRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                name = entity.name.value,
                legalName = null,
                email = entity.email?.value,
                phone = entity.phone?.value,
                cnpj = entity.cnpj?.value,
                description = entity.description,
                companyTypeId = entity.companyType.id.internalId,
                addressId = entity.addressId?.value,
                isPublic = entity.isPublic,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        company: CompaniesRecord,
        settings: CompanySettingsRecord?,
        companyType: CompanyTypesRecord,
    ): Result<CompanyEntity> =
        errorDataConversion {
            CompanyEntity(
                id =
                    EntityId(
                        internalId = company.id!!,
                        publicId = company.publicId,
                    ),
                name = CompanyName.restore(company.name),
                email = company.email?.let { EmailAddress.restore(it) },
                phone = company.phone?.let { PhoneNumber.restore(it) },
                cnpj = company.cnpj?.let { Cnpj.restore(it) },
                description = company.description,
                companyType = companyTypePersistenceMapper.toEntity(companyType).getOrThrow(),
                addressId = company.addressId?.let { AddressId.restore(it) },
                settings =
                    settings?.let { companySettingsPersistenceMapper.toEntity(it).getOrThrow() } ?: CompanySettingsEntity.createNew(
                        companyId = CompanyId.restore(company.id!!),
                    ),
                isPublic = company.isPublic!!,
                audit =
                    EntityAudit(
                        createdAt = company.createdAt!!,
                        updatedAt = company.updatedAt!!,
                        deletedAt = company.deletedAt,
                        createdBy = company.createdBy,
                        updatedBy = company.updatedBy,
                        version = company.version!!,
                    ),
            )
        }.onFailure { log.error(it) { "Erro ao converter CompanyRecord para CompanyEntity" } }
}

@Component
class CompanyMapper(
    private val searchAddressUseCase: SearchAddressUseCase,
    private val companySettingsMapper: CompanySettingsMapper,
    private val companyTypeMapper: CompanyTypeMapper,
    private val addressMapper: AddressMapper,
) {
    fun toDTO(entity: CompanyEntity): Result<CompanyDTO> =
        runCatching {
            val address = entity.addressId?.let { searchAddressUseCase.getById(it.value).getOrNull() }
            CompanyDTO(
                id = entity.id,
                name = entity.name.value,
                email = entity.email?.value,
                phone = entity.phone?.value,
                cnpj = entity.cnpj?.value,
                description = entity.description,
                type = companyTypeMapper.toDTO(entity.companyType),
                address = address,
                settings = entity.settings?.let { companySettingsMapper.toDTO(it).getOrThrow() },
                isPublic = entity.isPublic,
                createdAt = entity.audit.createdAt,
            )
        }

    fun toResponse(dto: CompanyDTO): CompanyResponse =
        CompanyResponse(
            id = dto.id.publicId,
            name = dto.name,
            email = dto.email,
            phone = dto.phone,
            cnpj = dto.cnpj,
            description = dto.description,
            type = companyTypeMapper.toResponse(dto.type),
            address = dto.address?.let { addressMapper.toResponse(it) },
            settings =
                dto.settings?.let {
                    companySettingsMapper.toResponse(
                        it,
                    )
                },
            isPublic = dto.isPublic,
            createdAt = dto.createdAt,
        )

    fun toSummaryResponse(dto: CompanyDTO): CompanySummaryResponse =
        CompanySummaryResponse(
            id = dto.id.publicId,
            name = dto.name,
            email = dto.email,
            phone = dto.phone,
            cnpj = dto.cnpj,
            description = dto.description,
            type = companyTypeMapper.toResponse(dto.type),
            settings = dto.settings?.let { companySettingsMapper.toResponse(it) },
            createdAt = dto.createdAt,
        )
}
