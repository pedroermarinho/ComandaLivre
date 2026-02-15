package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user.AssignUserToGroupForm

interface UserGroupRepository {
    fun create(form: AssignUserToGroupForm): Result<Unit>

    fun checkUserInGroup(
        userId: Int,
        featureGroupId: Int,
    ): Boolean

    fun exists(
        userId: Int,
        featureGroupId: Int,
    ): Boolean

    fun hasAllPermissions(
        id: Int,
        features: List<String>,
    ): Boolean

    fun hasAnyPermission(
        id: Int,
        features: List<String>,
    ): Boolean

    fun getFeatureKeysByUserId(userId: Int): Result<List<String>>

    fun changeEnabled(
        userId: Int,
        groupId: Int,
        enabled: Boolean,
    ): Result<Unit>
}
