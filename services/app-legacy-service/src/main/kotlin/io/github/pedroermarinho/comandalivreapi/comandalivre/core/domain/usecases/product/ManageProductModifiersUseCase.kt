package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierGroupEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierOptionEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.ProductModifierGroupForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.ProductModifierOptionForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductModifierRepository
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ManageProductModifiersUseCase(
    private val productModifierRepository: ProductModifierRepository,
    private val searchProductUseCase: SearchProductUseCase,
) {
    fun createGroup(
        productId: UUID,
        form: ProductModifierGroupForm,
    ): Result<ProductModifierGroupEntity> {
        if (form.minSelection > form.maxSelection) {
            return Result.failure(BusinessLogicException("A seleção mínima não pode ser maior que a máxima."))
        }
        val product = searchProductUseCase.getById(productId).getOrThrow()
        return productModifierRepository.createGroup(product.id.internalId, form)
    }

    fun createOption(
        groupId: UUID,
        form: ProductModifierOptionForm,
    ): Result<ProductModifierOptionEntity> {
        val group = productModifierRepository.getGroupById(groupId).getOrThrow()
        val options = productModifierRepository.getOptionsByGroup(group.id.internalId).getOrThrow()
        if (group.maxSelection > 0 && options.size >= group.maxSelection) {
            return Result.failure(BusinessLogicException("O número máximo de opções para este grupo já foi atingido."))
        }
        return productModifierRepository.createOption(group.id.internalId, form)
    }
}
