package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FilePathEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.asset.RegisterAssetUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.asset.RegisterAssetUseCase.AssetUploadParams
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.FileTypes
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.validateImageFile
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Transactional
@UseCase
class UpdateCompanyImageUseCase(
    private val companyRepository: CompanyRepository,
    private val registerAssetUseCase: RegisterAssetUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val checkPermissionCompanyUseCase: CheckPermissionCompanyUseCase,
    private val createCompanyUseCase: CreateCompanyUseCase,
) {
    fun updateLogo(
        companyPublicId: UUID,
        imageFile: MultipartFile,
    ): Result<Unit> =
        runCatching {
            checkPermissionCompanyUseCase.execute(companyPublicId).getOrThrow()
            validateImageFile(imageFile, listOf(FileTypes.PNG, FileTypes.JPG, FileTypes.WEBP)).getOrThrow()
            val company = companyRepository.getById(companyPublicId).getOrThrow()

            val asset =
                registerAssetUseCase
                    .execute(
                        params =
                            AssetUploadParams(
                                file = imageFile,
                                storagePath = FilePathEnum.COMPANY_IMAGES,
                                tags = listOf("company", "logo", companyPublicId.toString()),
                            ),
                    ).getOrThrow()

            companyRepository.save(company.updateLogo(asset.internalId)).getOrThrow()
        }

    fun updateBanner(
        companyPublicId: UUID,
        imageFile: MultipartFile,
    ): Result<Unit> =
        runCatching {
            checkPermissionCompanyUseCase.execute(companyPublicId).getOrThrow()
            validateImageFile(imageFile, listOf(FileTypes.PNG, FileTypes.JPG, FileTypes.WEBP)).getOrThrow()
            val company = companyRepository.getById(companyPublicId).getOrThrow()

            val asset =
                registerAssetUseCase
                    .execute(
                        params =
                            AssetUploadParams(
                                file = imageFile,
                                storagePath = FilePathEnum.COMPANY_IMAGES,
                                tags = listOf("company", "banner", companyPublicId.toString()),
                            ),
                    ).getOrThrow()

            companyRepository.save(company.updateBanner(asset.internalId)).getOrThrow()
        }
}
