package io.github.pedroermarinho.company.domain.enums

import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType

enum class CompanyTypeEnum(
    val value: String,
) {
    RESTAURANT("restaurant"),
    CONSTRUCTION_COMPANY("construction_company"),
    ;

    fun matches(value: String): Boolean = this.value.equals(value, ignoreCase = true)

    companion object {
        fun from(companyType: CompanyType): CompanyTypeEnum? = entries.firstOrNull { it.matches(companyType.key.value) }
    }
}
