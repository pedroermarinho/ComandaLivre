package io.github.pedroermarinho.comandalivre.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategory
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface ProductCategoryRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<ProductCategory>>

    fun getAll(): Result<List<ProductCategory>>

    fun getById(id: Int): Result<ProductCategory>

    fun getById(id: UUID): Result<ProductCategory>

    fun getByKey(key: String): Result<ProductCategory>

    fun getIdByPublicId(id: UUID): Result<EntityId>

    fun save(entity: ProductCategory): Result<EntityId>
}
