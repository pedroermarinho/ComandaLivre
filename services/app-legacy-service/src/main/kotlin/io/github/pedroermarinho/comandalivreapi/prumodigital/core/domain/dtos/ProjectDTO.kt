package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.address.AddressDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class ProjectDTO(
    val id: EntityId,
    val company: CompanyDTO,
    val name: String,
    val code: String,
    val address: AddressDTO?,
    val plannedStartDate: LocalDate?,
    val plannedEndDate: LocalDate?,
    val actualStartDate: LocalDate?,
    val actualEndDate: LocalDate?,
    val clientName: String?,
    val projectStatus: ProjectStatusDTO,
    val budget: BigDecimal?,
    val description: String?,
    val createdAt: LocalDateTime,
)
