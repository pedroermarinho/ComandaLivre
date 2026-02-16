package io.github.pedroermarinho.comandalivre.domain.usecases.product

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.product.ProductCreateRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateProductUseCase(
    private val productRepository: ProductRepository,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchProductCategoryUseCase: SearchProductCategoryUseCase,
) {
    private val log = KotlinLogging.logger {}

    companion object {
        private const val DEFAULT_SERVES_PERSONS = 1
    }

    fun create(form: ProductCreateRequest): Result<EntityId> =
        runCatching {
            val companyId = searchCompanyUseCase.getIdById(form.companyId).getOrThrow()
            val category = searchProductCategoryUseCase.getById(form.categoryId).getOrThrow()
            productRepository
                .save(
                    ProductEntity.createNew(
                        publicId = form.publicId,
                        name = form.name,
                        price = form.price,
                        category = category,
                        description = form.description,
                        servesPersons = form.servesPersons ?: DEFAULT_SERVES_PERSONS,
                        availability = form.availability,
                        companyId = companyId,
                        ingredients = form.ingredients,
                    ),
                ).getOrThrow()
        }
}
