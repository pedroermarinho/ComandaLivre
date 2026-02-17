package io.github.pedroermarinho.comandalivre.data.repositories

import com.github.f4b6a3.uuid.UuidCreator
import comandalivre.tables.references.PRODUCT_MODIFIERS_GROUPS
import comandalivre.tables.references.PRODUCT_MODIFIERS_OPTIONS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierGroupEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierOptionEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.ProductModifierGroupForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.ProductModifierOptionForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductModifierRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductModifierGroupPersistenceMapper
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductModifierOptionPersistenceMapper
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JooqProductModifierRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val productModifierGroupPersistenceMapper: ProductModifierGroupPersistenceMapper,
    private val productModifierOptionPersistenceMapper: ProductModifierOptionPersistenceMapper,
) : ProductModifierRepository {
    override fun createGroup(
        productId: Int,
        form: ProductModifierGroupForm,
    ): Result<ProductModifierGroupEntity> =
        runCatching {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            val result =
                dsl
                    .insertInto(PRODUCT_MODIFIERS_GROUPS)
                    .set(PRODUCT_MODIFIERS_GROUPS.PUBLIC_ID, UuidCreator.getTimeOrderedEpoch())
                    .set(PRODUCT_MODIFIERS_GROUPS.PRODUCT_ID, productId)
                    .set(PRODUCT_MODIFIERS_GROUPS.NAME, form.name)
                    .set(PRODUCT_MODIFIERS_GROUPS.MIN_SELECTION, form.minSelection)
                    .set(PRODUCT_MODIFIERS_GROUPS.MAX_SELECTION, form.maxSelection)
                    .set(PRODUCT_MODIFIERS_GROUPS.DISPLAY_ORDER, form.displayOrder)
                    .set(PRODUCT_MODIFIERS_GROUPS.CREATED_BY, userAuth.sub)
                    .returning()
                    .fetchOne()!!
            productModifierGroupPersistenceMapper.toEntity(result).getOrThrow()
        }

    override fun createOption(
        groupId: Int,
        form: ProductModifierOptionForm,
    ): Result<ProductModifierOptionEntity> =
        runCatching {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            val result =
                dsl
                    .insertInto(PRODUCT_MODIFIERS_OPTIONS)
                    .set(PRODUCT_MODIFIERS_OPTIONS.PUBLIC_ID, UuidCreator.getTimeOrderedEpoch())
                    .set(PRODUCT_MODIFIERS_OPTIONS.MODIFIER_GROUP_ID, groupId)
                    .set(PRODUCT_MODIFIERS_OPTIONS.NAME, form.name)
                    .set(PRODUCT_MODIFIERS_OPTIONS.PRICE_CHANGE, form.priceChange)
                    .set(PRODUCT_MODIFIERS_OPTIONS.IS_DEFAULT, form.isDefault)
                    .set(PRODUCT_MODIFIERS_OPTIONS.DISPLAY_ORDER, form.displayOrder)
                    .set(PRODUCT_MODIFIERS_OPTIONS.CREATED_BY, userAuth.sub)
                    .returning()
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível criar a opção de modificador")
            productModifierOptionPersistenceMapper.toEntity(result).getOrThrow()
        }

    override fun getGroupsByProduct(productId: Int): Result<List<ProductModifierGroupEntity>> =
        runCatching {
            dsl
                .selectFrom(PRODUCT_MODIFIERS_GROUPS)
                .where(PRODUCT_MODIFIERS_GROUPS.PRODUCT_ID.eq(productId))
                .and(PRODUCT_MODIFIERS_GROUPS.DELETED_AT.isNull)
                .fetch()
                .map { productModifierGroupPersistenceMapper.toEntity(it).getOrThrow() }
        }

    override fun getOptionsByGroup(groupId: Int): Result<List<ProductModifierOptionEntity>> =
        runCatching {
            dsl
                .selectFrom(PRODUCT_MODIFIERS_OPTIONS)
                .where(PRODUCT_MODIFIERS_OPTIONS.MODIFIER_GROUP_ID.eq(groupId))
                .and(PRODUCT_MODIFIERS_OPTIONS.DELETED_AT.isNull)
                .fetch()
                .map { productModifierOptionPersistenceMapper.toEntity(it).getOrThrow() }
        }

    override fun getGroupById(groupId: UUID): Result<ProductModifierGroupEntity> =
        runCatching {
            val result =
                dsl
                    .selectFrom(PRODUCT_MODIFIERS_GROUPS)
                    .where(PRODUCT_MODIFIERS_GROUPS.PUBLIC_ID.eq(groupId))
                    .and(PRODUCT_MODIFIERS_GROUPS.DELETED_AT.isNull)
                    .fetchOne() ?: throw NotFoundException("Grupo de modificadores não encontrado")
            productModifierGroupPersistenceMapper.toEntity(result).getOrThrow()
        }

    override fun getOptionById(optionId: UUID): Result<ProductModifierOptionEntity> =
        runCatching {
            val result =
                dsl
                    .selectFrom(PRODUCT_MODIFIERS_OPTIONS)
                    .where(PRODUCT_MODIFIERS_OPTIONS.PUBLIC_ID.eq(optionId))
                    .and(PRODUCT_MODIFIERS_OPTIONS.DELETED_AT.isNull)
                    .fetchOne() ?: throw NotFoundException("Opção de modificador não encontrada")
            productModifierOptionPersistenceMapper.toEntity(result).getOrThrow()
        }

    override fun getOptionIdsByPublicIds(optionIds: List<UUID>): Result<List<EntityId>> =
        runCatching {
            dsl
                .select(PRODUCT_MODIFIERS_OPTIONS.ID, PRODUCT_MODIFIERS_OPTIONS.PUBLIC_ID)
                .from(PRODUCT_MODIFIERS_OPTIONS)
                .where(PRODUCT_MODIFIERS_OPTIONS.PUBLIC_ID.`in`(optionIds))
                .and(PRODUCT_MODIFIERS_OPTIONS.DELETED_AT.isNull)
                .fetch()
                .map { EntityId(it.value1()!!, it.value2()!!) }
        }

    override fun saveGroup(entity: ProductModifierGroupEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = productModifierGroupPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(PRODUCT_MODIFIERS_GROUPS)
                        .set(record)
                        .where(PRODUCT_MODIFIERS_GROUPS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o grupo de modificadores")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(PRODUCT_MODIFIERS_GROUPS)
                    .set(record)
                    .returning(PRODUCT_MODIFIERS_GROUPS.ID, PRODUCT_MODIFIERS_GROUPS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o grupo de modificadores")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    override fun saveOption(entity: ProductModifierOptionEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = productModifierOptionPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(PRODUCT_MODIFIERS_OPTIONS)
                        .set(record)
                        .where(PRODUCT_MODIFIERS_OPTIONS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a opção de modificador")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(PRODUCT_MODIFIERS_OPTIONS)
                    .set(record)
                    .returning(PRODUCT_MODIFIERS_OPTIONS.ID, PRODUCT_MODIFIERS_OPTIONS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a opção de modificador")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
