package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.EmployeeProjectAssignmentEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.project.EmployeeProjectAssignmentForm
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface EmployeeProjectAssignmentRepository {
    fun create(form: EmployeeProjectAssignmentForm): Result<EntityId>

    fun getAll(
        pageable: PageableDTO,
        projectId: Int,
    ): Result<PageDTO<EmployeeProjectAssignmentEntity>>

    fun getAll(): Result<List<EmployeeProjectAssignmentEntity>>

    fun getByProjectIdAndEmployeeId(
        projectId: Int,
        employeeId: Int,
    ): Result<EmployeeProjectAssignmentEntity>

    fun getById(id: Int): Result<EmployeeProjectAssignmentEntity>

    fun getById(id: UUID): Result<EmployeeProjectAssignmentEntity>

    fun existByProjectIdAndEmployeeId(
        projectId: Int,
        employeeId: Int,
    ): Boolean

    fun save(entity: EmployeeProjectAssignmentEntity): Result<EntityId>
}
