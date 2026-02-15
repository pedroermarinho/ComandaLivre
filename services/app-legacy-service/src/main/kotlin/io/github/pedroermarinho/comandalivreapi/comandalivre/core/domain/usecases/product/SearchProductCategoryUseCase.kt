package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductCategoryDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductCategoryRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategory
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductCategoryMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.stereotype.Service
import java.util.*

@Service
class SearchProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepository,
    private val productCategoryMapper: ProductCategoryMapper,
) {
    fun getAll(pageable: PageableDTO): Result<PageDTO<ProductCategoryDTO>> =
        productCategoryRepository.getAll(pageable).map { it.map { entity -> productCategoryMapper.toDTO(entity) } }

    fun getAll(): Result<List<ProductCategoryDTO>> = productCategoryRepository.getAll().map { it.map { entity -> productCategoryMapper.toDTO(entity) } }

    fun getById(id: Int): Result<ProductCategoryDTO> = productCategoryRepository.getById(id).map { productCategoryMapper.toDTO(it) }

    fun getById(id: UUID): Result<ProductCategory> = productCategoryRepository.getById(id)

    fun getIdByPublicId(id: UUID): Result<EntityId> = productCategoryRepository.getIdByPublicId(id)

    fun getByKey(key: String): Result<ProductCategoryDTO> = productCategoryRepository.getByKey(key).map { productCategoryMapper.toDTO(it) }
}
