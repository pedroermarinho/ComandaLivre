package io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company

data class CompanyCountByTypeDTO(
    val typeName: String,
    val companyCount: Long,
)
