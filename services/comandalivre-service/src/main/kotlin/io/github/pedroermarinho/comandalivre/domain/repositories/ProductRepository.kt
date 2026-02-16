package io.github.pedroermarinho.comandalivre.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductEntity
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface ProductRepository {
    fun getAll(
        pageable: PageableDTO,
        companyPublicId: UUID,
    ): Result<PageDTO<ProductEntity>>

    fun getById(id: UUID): Result<ProductEntity>

    fun getById(id: Int): Result<ProductEntity>

    fun existsByPublicId(publicId: UUID): Boolean

    fun save(entity: ProductEntity): Result<EntityId>
}
