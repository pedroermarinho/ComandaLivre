package io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeInviteEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

interface EmployeeInviteRepository {
    fun getById(id: UUID): Result<EmployeeInviteEntity>

    fun getById(id: Int): Result<EmployeeInviteEntity>

    fun getBySub(
        pageable: PageableDTO,
        sub: String,
    ): Result<PageDTO<EmployeeInviteEntity>>

    fun getByCompanyId(
        companyId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<EmployeeInviteEntity>>

    fun save(entity: EmployeeInviteEntity): Result<EntityId>
}
