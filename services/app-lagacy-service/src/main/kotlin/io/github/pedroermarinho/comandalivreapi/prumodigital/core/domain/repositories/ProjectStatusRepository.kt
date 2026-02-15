package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ProjectStatusEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

interface ProjectStatusRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<ProjectStatusEntity>>

    fun getAll(): Result<List<ProjectStatusEntity>>

    fun getById(id: UUID): Result<ProjectStatusEntity>

    fun getById(id: Int): Result<ProjectStatusEntity>

    fun getByKey(key: String): Result<ProjectStatusEntity>

    fun save(entity: ProjectStatusEntity): Result<EntityId>
}
