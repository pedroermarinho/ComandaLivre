package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.FeatureEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user.GrantPermissionForm
import java.util.*

interface GroupPermissionRepository {
    fun create(form: GrantPermissionForm): Result<Unit>

    fun checkFeatureInGroup(
        featureId: Int,
        featureGroupId: Int,
    ): Boolean

    fun getAll(
        groupId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<FeatureEntity>>

    fun exists(
        featureId: Int,
        groupId: Int,
    ): Boolean

    fun changeEnabled(
        featureId: Int,
        groupId: Int,
        enabled: Boolean,
    ): Result<Unit>
}
