package io.github.pedroermarinho.prumodigital.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ProjectStatusEntity
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface ProjectStatusRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<ProjectStatusEntity>>

    fun getAll(): Result<List<ProjectStatusEntity>>

    fun getById(id: UUID): Result<ProjectStatusEntity>

    fun getById(id: Int): Result<ProjectStatusEntity>

    fun getByKey(key: String): Result<ProjectStatusEntity>

    fun save(entity: ProjectStatusEntity): Result<EntityId>
}
