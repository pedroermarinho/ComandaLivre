package io.github.pedroermarinho.user.domain.usecases.user

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserFilterDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserRegistrationsPerDayDTO
import io.github.pedroermarinho.user.domain.entities.UserEntity
import io.github.pedroermarinho.user.domain.enums.CacheConstants
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.user.domain.repositories.UserRepository
import io.github.pedroermarinho.user.domain.usecases.group.SearchUserGroupUseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.user.infra.mappers.UserMapper
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchUserUseCase(
    private val userRepository: UserRepository,
    private val searchUserGroupUseCase: SearchUserGroupUseCase,
    private val userMapper: UserMapper,
) {
    private val log = KotlinLogging.logger {}

    @Cacheable(value = [CacheConstants.USER], key = "#sub", unless = "#result == false")
    fun existsUserBySub(sub: String): Boolean = userRepository.existsBySub(sub)

    fun getAll(
        pageable: PageableDTO,
        filter: UserFilterDTO,
    ): Result<PageDTO<UserDTO>> =
        runCatching {
            userRepository.getAll(pageable, filter).map { it.map { entity -> convertToDTO(entity) } }.getOrThrow()
        }

    fun getBySub(sub: String): Result<UserDTO> {
        if (sub.isEmpty()) {
            return Result.failure(BusinessLogicException("Sub está vazio"))
        }
        return userRepository.getBySub(sub).map { entity -> convertToDTO(entity) }
    }

    fun getEntityBySub(sub: String): Result<UserEntity> {
        if (sub.isEmpty()) {
            return Result.failure(BusinessLogicException("Sub está vazio"))
        }
        return userRepository.getBySub(sub)
    }

    fun getByEmail(email: String): Result<UserDTO> = userRepository.getByEmail(email).map { entity -> convertToDTO(entity) }

    fun getById(id: Int): Result<UserDTO> = userRepository.getById(id).map { entity -> convertToDTO(entity) }

    fun getById(id: UUID): Result<UserDTO> = userRepository.getById(id).map { entity -> convertToDTO(entity) }

    fun getEntityById(id: UUID): Result<UserEntity> = userRepository.getById(id)

    fun getIdBySub(sub: String): Result<Int> {
        if (sub.isEmpty()) {
            return Result.failure(BusinessLogicException("Sub está vazio"))
        }
        return userRepository.getIdBySub(sub)
    }

    fun getIdById(id: UUID): Result<EntityId> = userRepository.getIdByPublicId(id)

    fun count(): Result<Long> = userRepository.count()

    fun getUserRegistrationsLastDays(days: Long): Result<List<UserRegistrationsPerDayDTO>> = userRepository.getUserRegistrationsLastDays(days)

    private fun convertToDTO(user: UserEntity): UserDTO {
        val featureKeys =
            searchUserGroupUseCase
                .getFeatureKeysByUserId(user.id.internalId)
                .onFailure { log.error(it) { "Erro ao buscar chaves de recursos do usuário ${user.id}" } }
                .getOrDefault(emptyList())
        return userMapper.toDTO(
            entity = user,
            featureKeys = featureKeys,
        )
    }
}
