package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class DeleteProductUseCase(
    private val productRepository: ProductRepository,
) {
    fun execute(publicId: UUID): Result<Unit> =
        runCatching {
            val product = productRepository.getById(publicId).getOrThrow()
            productRepository.save(product.delete())
        }
}
