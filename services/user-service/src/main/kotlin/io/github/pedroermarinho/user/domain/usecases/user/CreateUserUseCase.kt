package io.github.pedroermarinho.user.domain.usecases.user

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.dtos.user.UserAuthDTO
import io.github.pedroermarinho.user.domain.entities.UserEntity
import io.github.pedroermarinho.user.domain.enums.GroupEnum
import io.github.pedroermarinho.user.domain.event.NewUserRegisteredEvent
import io.github.pedroermarinho.user.domain.repositories.UserRepository
import io.github.pedroermarinho.user.domain.usecases.group.AddUserToGroupUseCase
import io.github.pedroermarinho.user.domain.usecases.group.SearchUserGroupUseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val addUserToGroupUseCase: AddUserToGroupUseCase,
    private val searchUserGroupUseCase: SearchUserGroupUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun execute(user: UserAuthDTO): Result<EntityId> =
        runCatching {
            val userEntity =
                UserEntity.createNew(
                    sub = user.sub,
                    name = user.name,
                    email = user.email,
                    avatarAssetId = null,
                )
            val newUserId = userRepository.save(userEntity).getOrThrow()

            log.info { "Usuário ${user.name}: ${user.email} criado com sucesso" }

            addUserToGroupUseCase
                .execute(
                    newUserId.publicId,
                    listOf(GroupEnum.DEFAULT_USER_CL, GroupEnum.DEFAULT_USER_PD),
                ).getOrThrow()

            log.info { "Usuário ${user.name} adicionado aos grupos padrão com sucesso" }

            onSuccessfulUserCreation(newUserId)

            return@runCatching newUserId
        }

    private fun onSuccessfulUserCreation(userId: EntityId) {
        val user: UserEntity = userRepository.getById(userId.internalId).getOrThrow()
        log.info { "O usuário ${user.name.value} de email ${user.email.value} foi criado com sucesso" }

        eventPublisher.publishEvent(
            NewUserRegisteredEvent(
                userId = user.id.internalId,
                userPublicId = user.id.publicId,
                sub = user.sub,
                name = user.name.value,
                email = user.email.value,
            ),
        )
    }
}
