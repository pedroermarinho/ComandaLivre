package io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyCountByTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanyEntity
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface CompanyRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<CompanyEntity>>

    fun getById(id: Int): Result<CompanyEntity>

    fun save(company: CompanyEntity): Result<EntityId>

    fun getById(id: UUID): Result<CompanyEntity>

    fun getPrivateIdByPublicId(companyPublicId: UUID): Result<Int>

    fun count(): Result<Long>

    fun countByType(): Result<List<CompanyCountByTypeDTO>>

    fun getSettingsIdByPublicId(companyPublicId: UUID): Result<Int>

    fun getAddressIdByCompanyId(companyId: UUID): Result<Int>

    fun getByDomain(domain: String): Result<CompanyEntity>

    fun existDomain(domain: String): Boolean

    fun existsByName(name: String): Boolean

    fun exists(id: UUID): Boolean
}
