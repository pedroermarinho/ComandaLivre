package io.github.pedroermarinho.user.data.repositories

import io.github.pedroermarinho.user.domain.entities.AssetEntity
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.user.domain.repositories.AssetRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.user.infra.mappers.AssetPersistenceMapper
import io.github.pedroermarinho.user.infra.mappers.toEntity
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import shared.tables.references.ASSETS
import java.util.*

@Repository
class JooqAssetRepository(
    private val dsl: DSLContext,
    private val assetPersistenceMapper: AssetPersistenceMapper,
) : AssetRepository {
    override fun getById(id: UUID): Result<AssetEntity> {
        val record =
            dsl
                .selectFrom(ASSETS)
                .where(ASSETS.PUBLIC_ID.eq(id))
                .and(ASSETS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Asset com ID público $id não encontrado."))
        return assetPersistenceMapper.toEntity(record)
    }

    override fun getById(id: Int): Result<AssetEntity> {
        val record =
            dsl
                .selectFrom(ASSETS)
                .where(ASSETS.ID.eq(id))
                .and(ASSETS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Asset com ID $id não encontrado."))

        return assetPersistenceMapper.toEntity(record)
    }

    override fun save(entity: AssetEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = assetPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(ASSETS)
                        .set(record)
                        .where(ASSETS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o asset")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(ASSETS)
                    .set(record)
                    .returning(ASSETS.ID, ASSETS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o asset")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
