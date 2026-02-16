package io.github.pedroermarinho.comandalivre.domain.usecases.product

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.product.ProductUpdateRequest
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateProductUseCase(
    private val productRepository: ProductRepository,
    private val searchProductCategoryUseCase: SearchProductCategoryUseCase,
) {
    fun update(
        publicId: UUID,
        product: ProductUpdateRequest,
    ): Result<Unit> =
        runCatching {
            val productCategory =
                searchProductCategoryUseCase
                    .getById(product.categoryId)
                    .getOrThrow()
            val productEntity = productRepository.getById(publicId).getOrThrow()
            val updatedProduct =
                productEntity.update(
                    name = product.name,
                    description = product.description,
                    price = product.price,
                    category = productCategory,
                    availability = product.availability,
                    servesPersons = product.servesPersons,
                    imageAssetId = productEntity.imageAssetId?.value,
                    ingredients = productEntity.ingredients,
                )
            productRepository.save(updatedProduct).map { Unit }.getOrThrow()
        }

    fun updateStatus(
        publicId: UUID,
        status: Boolean,
    ): Result<Unit> =
        runCatching {
            val product = productRepository.getById(publicId).getOrThrow()
            productRepository.save(product.updateStatus(status)).getOrThrow()
        }
}
