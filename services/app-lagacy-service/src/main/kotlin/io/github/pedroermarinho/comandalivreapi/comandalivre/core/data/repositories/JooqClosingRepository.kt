package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.CASH_REGISTER_CLOSINGS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ClosingEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ClosingRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ClosingPersistenceMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqClosingRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val closingPersistenceMapper: ClosingPersistenceMapper,
) : ClosingRepository {
    override fun getBySessionId(sessionId: Int): Result<ClosingEntity> =
        runCatching {
            val record =
                query()
                    .where(CASH_REGISTER_CLOSINGS.SESSION_ID.eq(sessionId))
                    .fetchOne() ?: return Result.failure(NotFoundException("Fechamento de caixa não encontrado para a sessão id: $sessionId"))

            closingPersistenceMapper.toEntity(record.into(CASH_REGISTER_CLOSINGS)).getOrThrow()
        }

    override fun getById(id: Int): Result<ClosingEntity> =
        runCatching {
            val record =
                query()
                    .where(CASH_REGISTER_CLOSINGS.ID.eq(id))
                    .fetchOne() ?: return Result.failure(NotFoundException("Fechamento de caixa não encontrado para o id: $id"))

            closingPersistenceMapper.toEntity(record.into(CASH_REGISTER_CLOSINGS)).getOrThrow()
        }

    private fun query() = dsl.select().from(CASH_REGISTER_CLOSINGS)

    override fun save(entity: ClosingEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = closingPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(CASH_REGISTER_CLOSINGS)
                        .set(record)
                        .where(CASH_REGISTER_CLOSINGS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o fechamento de caixa")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(CASH_REGISTER_CLOSINGS)
                    .set(record)
                    .returning(CASH_REGISTER_CLOSINGS.ID, CASH_REGISTER_CLOSINGS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o fechamento de caixa")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
