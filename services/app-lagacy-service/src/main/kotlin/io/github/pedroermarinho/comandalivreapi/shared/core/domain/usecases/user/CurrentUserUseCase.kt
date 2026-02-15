package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.UserEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import org.springframework.transaction.annotation.Transactional
import java.util.*

@UseCase
class CurrentUserUseCase(
    private val currentUserService: CurrentUserService,
    private val createUserUseCase: CreateUserUseCase,
    private val searchUserUseCase: SearchUserUseCase,
) {
    // TODO: colocar em um usecase separado
    @Transactional
    fun getOrCreate(): Result<UserDTO> =
        runCatching {
            val user = currentUserService.getLoggedUser().getOrThrow()

            val userExists = searchUserUseCase.existsUserBySub(user.sub)

            if (userExists) {
                return searchUserUseCase.getBySub(user.sub)
            }

            val newUserId = createUserUseCase.execute(user).getOrThrow()

            return searchUserUseCase.getById(newUserId.internalId)
        }

    @Transactional(readOnly = true)
    fun getUserId(): Result<Int> =
        runCatching {
            val user = currentUserService.getLoggedUser().getOrThrow()
            return searchUserUseCase.getIdBySub(user.sub)
        }

    @Transactional(readOnly = true)
    fun getUser(): Result<UserDTO> =
        runCatching {
            val user = currentUserService.getLoggedUser().getOrThrow()
            return searchUserUseCase.getBySub(user.sub)
        }

    @Transactional(readOnly = true)
    fun getUserEntity(): Result<UserEntity> =
        runCatching {
            val user = currentUserService.getLoggedUser().getOrThrow()
            return searchUserUseCase.getEntityBySub(user.sub)
        }

    @Transactional(readOnly = true)
    fun checkUserEditingOwnResource(userId: UUID): Result<Unit> =
        runCatching {
            val user = this.getUser().getOrThrow()
            if (user.id.publicId != userId) {
                return Result.failure(BusinessLogicException("Usuário não pode editar o recurso"))
            }
            return Result.success(Unit)
        }

    @Transactional(readOnly = true)
    fun checkUserEditingOwnResource(userId: Int): Result<Unit> =
        runCatching {
            val currentId = this.getUserId().getOrThrow()
            if (userId != currentId) {
                return Result.failure(BusinessLogicException("Usuário não pode editar o recurso"))
            }
            return Result.success(Unit)
        }

    @Transactional(readOnly = true)
    fun checkUserEditingOwnResource(userSub: String): Result<Unit> =
        runCatching {
            val user = currentUserService.getLoggedUser().getOrThrow()
            if (user.sub != userSub) {
                return Result.failure(BusinessLogicException("Usuário não pode editar o recurso"))
            }
            return Result.success(Unit)
        }
}
