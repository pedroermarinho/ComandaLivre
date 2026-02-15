package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductWithModifiersDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductModifierRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductMapper
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductModifierGroupMapper
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductModifierOptionMapper
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductWithModifiersMapper
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.asset.GetUrlAssetUseCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class SearchProductUseCase(
    private val productRepository: ProductRepository,
    private val productModifierRepository: ProductModifierRepository,
    private val searchProductCategoryUseCase: SearchProductCategoryUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val getUrlAssetUseCase: GetUrlAssetUseCase,
    private val productMapper: ProductMapper,
    private val productWithModifiersMapper: ProductWithModifiersMapper,
    private val productModifierGroupMapper: ProductModifierGroupMapper,
    private val productModifierOptionMapper: ProductModifierOptionMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getByCompany(
        pageable: PageableDTO,
        companyId: UUID,
    ): Result<PageDTO<ProductDTO>> = productRepository.getAll(pageable, companyId).map { page -> page.map { convert(it).getOrThrow() } }

    fun getByIdWithModifiers(productId: UUID): Result<ProductWithModifiersDTO> =
        runCatching {
            productRepository.getById(productId).map { convertWithModifiers(it).getOrThrow() }.getOrThrow()
        }

    fun getById(productId: UUID): Result<ProductDTO> = runCatching { productRepository.getById(productId).map { convert(it).getOrThrow() }.getOrThrow() }

    fun getEntityById(productId: UUID): Result<ProductEntity> = productRepository.getById(productId)

    fun getById(id: Int): Result<ProductDTO> =
        runCatching {
            productRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun existsByPublicId(publicId: UUID): Boolean = productRepository.existsByPublicId(publicId)

    private fun convert(entity: ProductEntity): Result<ProductDTO> =
        runCatching {
            productMapper.toDTO(
                entity = entity,
                company = searchCompanyUseCase.getById(entity.companyId.value).getOrThrow(),
                image = entity.imageAssetId?.let { getUrlAssetUseCase.execute(it.value).getOrNull() },
            )
        }.onFailure { log.error(it) { "Erro ao converter ProductEntity para ProductDTO para o ID da entidade: ${entity.id}" } }

    private fun convertWithModifiers(entity: ProductEntity): Result<ProductWithModifiersDTO> =
        runCatching {
            val productDTO = convert(entity).getOrThrow()
            val modifierGroups = productModifierRepository.getGroupsByProduct(entity.id.internalId).getOrThrow()
            val modifierGroupDTOs =
                modifierGroups.map { group ->
                    val options = productModifierRepository.getOptionsByGroup(group.id.internalId).getOrThrow()
                    productModifierGroupMapper.toDTO(
                        entity = group,
                        options =
                            options.map { option ->
                                productModifierOptionMapper.toDTO(
                                    entity = option,
                                    image = option.imageAssetId?.let { getUrlAssetUseCase.execute(it.value).getOrNull() },
                                )
                            },
                    )
                }
            productWithModifiersMapper.toDTO(product = productDTO, modifierGroups = modifierGroupDTOs)
        }.onFailure { log.error(it) { "Erro ao converter ProductEntity para ProductWithModifiersDTO para o ID da entidade: ${entity.id}" } }
}
