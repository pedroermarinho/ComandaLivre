package io.github.pedroermarinho.company.domain.repositories

import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeInviteEntity
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
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
