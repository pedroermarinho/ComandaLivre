package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.UserRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class DeleteUserUseCase(
    private val userRepository: UserRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val searchUserUseCase: SearchUserUseCase,
) {
    fun execute(id: UUID): Result<Unit> =
        runCatching {
            val user = searchUserUseCase.getEntityById(id).getOrThrow()
            val currentId = currentUserUseCase.getUserId().getOrThrow()

            if (user.id.internalId != currentId) {
                return Result.failure(BusinessLogicException("Você não tem permissão para excluir este usuário"))
            }

            userRepository.save(user.delete()).getOrThrow()
        }
}
