package io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeInviteStatus
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface EmployeeInviteStatusRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<EmployeeInviteStatus>>

    fun getById(id: Int): Result<EmployeeInviteStatus>

    fun getById(id: UUID): Result<EmployeeInviteStatus>

    fun getByKey(key: String): Result<EmployeeInviteStatus>

    fun save(entity: EmployeeInviteStatus): Result<EntityId>
}
