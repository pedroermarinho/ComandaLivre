package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FilePathEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.asset.RegisterAssetUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.asset.RegisterAssetUseCase.AssetUploadParams
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.FileTypes
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.validateImageFile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class UpdateProductImageUseCase(
    private val productRepository: ProductRepository,
    private val searchProductUseCase: SearchProductUseCase,
    private val registerAssetUseCase: RegisterAssetUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun execute(
        id: UUID,
        imageFile: MultipartFile,
    ): Result<Unit> =
        runCatching {
            val product = searchProductUseCase.getEntityById(id).getOrThrow()

            validateImageFile(imageFile, listOf(FileTypes.PNG, FileTypes.JPG)).onFailure {
                log.error { "Erro na validação do arquivo de imagem: $it" }
                return Result.failure(it)
            }

            val assetId =
                registerAssetUseCase
                    .execute(
                        params =
                            AssetUploadParams(
                                file = imageFile,
                                storagePath = FilePathEnum.PRODUCT_IMAGES,
                                tags = listOf("product", "image"),
                            ),
                    ).getOrThrow()

            productRepository
                .save(
                    product.updateImage(
                        imageId = assetId.internalId,
                    ),
                ).getOrThrow()
        }
}
