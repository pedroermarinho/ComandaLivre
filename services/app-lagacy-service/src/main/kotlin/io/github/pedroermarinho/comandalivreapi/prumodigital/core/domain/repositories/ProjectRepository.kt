package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectCountByStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ProjectEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.project.ProjectCreateForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

interface ProjectRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<ProjectEntity>>

    fun create(form: ProjectCreateForm): Result<EntityId>

    fun getById(id: UUID): Result<ProjectEntity>

    fun getById(id: Int): Result<ProjectEntity>

    fun getByCodeAndCompanyId(
        code: String,
        companyId: Int,
    ): Result<ProjectEntity>

    fun updateStatus(
        publicId: UUID,
        statusId: Int,
    ): Result<Unit>

    fun update(
        publicId: UUID,
        form: ProjectCreateForm,
    ): Result<Unit>

    fun getProjectCountByStatus(): Result<List<ProjectCountByStatusDTO>>

    fun save(entity: ProjectEntity): Result<EntityId>
}
