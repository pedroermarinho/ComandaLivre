package io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

interface CompanyTypeRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<CompanyType>>

    fun getAll(): Result<List<CompanyType>>

    fun getById(id: Int): Result<CompanyType>

    fun getByKey(key: String): Result<CompanyType>

    fun getById(id: UUID): Result<CompanyType>

    fun save(entity: CompanyType): Result<EntityId>
}
