package io.github.pedroermarinho.user.domain.usecases.group

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.enums.FeatureEnum
import io.github.pedroermarinho.user.domain.repositories.UserGroupRepository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class SearchUserGroupUseCase(
    private val userGroupRepository: UserGroupRepository,
) {
    fun getFeatureKeysByUserId(userId: Int): Result<List<String>> = userGroupRepository.getFeatureKeysByUserId(userId)

    fun checkUserInGroup(
        userId: Int,
        groupId: Int,
    ): Boolean =
        userGroupRepository.checkUserInGroup(
            userId = userId,
            featureGroupId = groupId,
        )

    fun hasAllPermissions(
        userId: Int,
        features: List<FeatureEnum>,
    ): Boolean {
        val featureKeys = features.map { it.value }
        return userGroupRepository.hasAllPermissions(userId, featureKeys)
    }

    fun hasAnyPermission(
        userId: Int,
        features: List<FeatureEnum>,
    ): Boolean {
        val featureKeys = features.map { it.value }
        return userGroupRepository.hasAnyPermission(userId, featureKeys)
    }

    fun exists(
        userId: Int,
        groupId: Int,
    ): Boolean = userGroupRepository.exists(userId, groupId)
}
