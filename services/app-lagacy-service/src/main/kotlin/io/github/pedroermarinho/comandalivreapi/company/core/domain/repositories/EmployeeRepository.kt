package io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

interface EmployeeRepository {
    fun getById(id: UUID): Result<EmployeeEntity>

    fun getById(id: Int): Result<EmployeeEntity>

    fun getByUserId(
        pageable: PageableDTO,
        userId: Int,
    ): Result<PageDTO<EmployeeEntity>>

    fun getByCompanyId(
        companyId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<EmployeeEntity>>

    fun isEmployeeOfCompany(
        userId: Int,
        companyId: Int,
    ): Result<Boolean>

    fun hasActiveCompanyRelation(userId: Int): Result<Boolean>

    fun getByCompanyId(
        companyId: Int,
        userId: Int,
    ): Result<EmployeeEntity>

    fun getByUserIdAndCompanyId(
        userId: Int,
        companyId: Int,
    ): Result<EmployeeEntity>

    fun save(entity: EmployeeEntity): Result<EntityId>
}
