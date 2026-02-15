package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierGroupEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierOptionEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.ProductModifierGroupForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.ProductModifierOptionForm
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.UUID

interface ProductModifierRepository {
    fun createGroup(
        productId: Int,
        form: ProductModifierGroupForm,
    ): Result<ProductModifierGroupEntity>

    fun createOption(
        groupId: Int,
        form: ProductModifierOptionForm,
    ): Result<ProductModifierOptionEntity>

    fun getGroupsByProduct(productId: Int): Result<List<ProductModifierGroupEntity>>

    fun getOptionsByGroup(groupId: Int): Result<List<ProductModifierOptionEntity>>

    fun getGroupById(groupId: UUID): Result<ProductModifierGroupEntity>

    fun getOptionById(optionId: UUID): Result<ProductModifierOptionEntity>

    fun getOptionIdsByPublicIds(optionIds: List<UUID>): Result<List<EntityId>>

    fun saveGroup(entity: ProductModifierGroupEntity): Result<EntityId>

    fun saveOption(entity: ProductModifierOptionEntity): Result<EntityId>
}
