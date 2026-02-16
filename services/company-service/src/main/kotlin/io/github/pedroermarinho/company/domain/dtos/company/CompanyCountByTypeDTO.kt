package io.github.pedroermarinho.company.domain.dtos.company

data class CompanyCountByTypeDTO(
    val typeName: String,
    val companyCount: Long,
)
