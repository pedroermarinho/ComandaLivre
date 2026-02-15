package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.TableEntity
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface TableRepository {
    fun getAll(
        pageable: PageableDTO,
        companyId: Int,
    ): Result<PageDTO<TableEntity>>

    fun getAllList(companyId: Int): Result<List<TableEntity>>

    fun getById(tablePublicId: UUID): Result<TableEntity>

    fun getById(id: Int): Result<TableEntity>

    fun getByIdUnsafe(id: Int): Result<TableEntity>

    fun existsById(id: UUID): Boolean

    fun existsByNameAndCompanyId(
        name: String,
        companyId: Int,
    ): Boolean

    fun save(entity: TableEntity): Result<EntityId>
}
