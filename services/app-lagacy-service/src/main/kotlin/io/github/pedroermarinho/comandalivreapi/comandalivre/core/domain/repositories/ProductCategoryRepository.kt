package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategory
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
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
