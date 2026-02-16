package io.github.pedroermarinho.comandalivre.repositories

import com.github.f4b6a3.uuid.UuidCreator
import comandalivre.tables.references.ORDER_ITEM_MODIFIERS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderItemModifierRepository
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqOrderItemModifierRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
) : OrderItemModifierRepository {
    override fun create(
        orderItemId: Int,
        modifierOptionId: Int,
    ): Result<EntityId> =
        runCatching {
            val userAuth = currentUserService.getLoggedUser().getOrElse { throw it }
            val result =
                dsl
                    .insertInto(ORDER_ITEM_MODIFIERS)
                    .set(ORDER_ITEM_MODIFIERS.PUBLIC_ID, UuidCreator.getTimeOrderedEpoch())
                    .set(ORDER_ITEM_MODIFIERS.ORDER_ITEM_ID, orderItemId)
                    .set(ORDER_ITEM_MODIFIERS.MODIFIER_OPTION_ID, modifierOptionId)
                    .set(ORDER_ITEM_MODIFIERS.CREATED_BY, userAuth.sub)
                    .returning(ORDER_ITEM_MODIFIERS.ID, ORDER_ITEM_MODIFIERS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o modificador do item do pedido")

            EntityId(result.id!!, result.publicId)
        }
}
