package io.github.pedroermarinho.user.domain.usecases.user

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.enums.FilePathEnum
import io.github.pedroermarinho.user.domain.forms.user.UpdateUserForm
import io.github.pedroermarinho.user.domain.repositories.UserRepository
import io.github.pedroermarinho.user.domain.usecases.asset.RegisterAssetUseCase
import io.github.pedroermarinho.user.domain.usecases.asset.RegisterAssetUseCase.AssetUploadParams
import io.github.pedroermarinho.shared.util.FileTypes
import io.github.pedroermarinho.shared.util.validateImageFile
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Transactional
@UseCase
class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val searchUserUseCase: SearchUserUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val registerAssetUseCase: RegisterAssetUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun execute(form: UpdateUserForm): Result<Unit> {
        log.info { "Iniciando atualização do usuário logado" }
        return runCatching {
            val currentUser = currentUserUseCase.getUserEntity().getOrThrow()
            userRepository
                .save(
                    currentUser.update(
                        name = form.name,
                    ),
                ).getOrThrow()
            return@runCatching
        }.onSuccess {
            log.info { "Usuário ${currentUserUseCase.getUser().getOrNull()?.id} atualizado com sucesso" }
        }.onFailure { e ->
            log.error(e) { "Falha ao atualizar usuário" }
        }
    }

    // TODO: fazer implementação do endpoint de update da foto do usuário
    fun updateProfilePhoto(
        id: UUID,
        imageFile: MultipartFile,
    ): Result<Unit> {
        val user = searchUserUseCase.getEntityById(id).getOrThrow()
        currentUserUseCase.checkUserEditingOwnResource(user.id.internalId).getOrThrow()
        validateImageFile(imageFile, listOf(FileTypes.PNG, FileTypes.JPG)).getOrThrow()
        return registerAssetUseCase
            .execute(
                params =
                    AssetUploadParams(
                        file = imageFile,
                        storagePath = FilePathEnum.PRODUCT_IMAGES,
                        tags = listOf("user", "image"),
                    ),
            ).map { userRepository.save(user.updatePhoto(it.internalId)) }
    }
}
