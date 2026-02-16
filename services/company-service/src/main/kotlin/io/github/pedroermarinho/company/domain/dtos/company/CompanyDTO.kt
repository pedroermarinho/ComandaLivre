package io.github.pedroermarinho.company.domain.dtos.company

import io.github.pedroermarinho.user.domain.dtos.address.AddressDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class CompanyDTO(
    val id: EntityId,
    val name: String,
    val email: String?,
    val phone: String?,
    val cnpj: String?,
    val description: String?,
    val type: CompanyTypeDTO,
    val address: AddressDTO?,
    val settings: CompanySettingsDTO?,
    val isPublic: Boolean,
    val createdAt: LocalDateTime,
)
