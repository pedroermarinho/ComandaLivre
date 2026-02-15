package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.CASH_REGISTER_SESSION_STATUS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.SessionStatusRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.SessionStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.SessionStatusPersistenceMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqSessionStatusRepository(
    private val dsl: DSLContext,
    private val sessionStatusPersistenceMapper: SessionStatusPersistenceMapper,
) : SessionStatusRepository {
    override fun getAll(): Result<List<SessionStatus>> =
        runCatching {
            query()
                .where(CASH_REGISTER_SESSION_STATUS.DELETED_AT.isNull)
                .fetch()
                .map { sessionStatusPersistenceMapper.toEntity(it.into(CASH_REGISTER_SESSION_STATUS)).getOrThrow() }
        }

    override fun getByKey(key: String): Result<SessionStatus> =
        runCatching {
            val result =
                query()
                    .where(CASH_REGISTER_SESSION_STATUS.KEY.eq(key))
                    .and(CASH_REGISTER_SESSION_STATUS.DELETED_AT.isNull)
                    .fetchOne() ?: throw NotFoundException("Status da sessão do caixa não encontrado")
            sessionStatusPersistenceMapper.toEntity(result.into(CASH_REGISTER_SESSION_STATUS)).getOrThrow()
        }

    override fun getById(id: Int): Result<SessionStatus> =
        runCatching {
            val result =
                query()
                    .where(CASH_REGISTER_SESSION_STATUS.ID.eq(id))
                    .and(CASH_REGISTER_SESSION_STATUS.DELETED_AT.isNull)
                    .fetchOne() ?: throw NotFoundException("Status da sessão do caixa não encontrado")
            sessionStatusPersistenceMapper.toEntity(result.into(CASH_REGISTER_SESSION_STATUS)).getOrThrow()
        }

    override fun save(entity: SessionStatus): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = sessionStatusPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(CASH_REGISTER_SESSION_STATUS)
                        .set(record)
                        .where(CASH_REGISTER_SESSION_STATUS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o status da sessão")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(CASH_REGISTER_SESSION_STATUS)
                    .set(record)
                    .returning(CASH_REGISTER_SESSION_STATUS.ID, CASH_REGISTER_SESSION_STATUS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o status da sessão")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    private fun query() = dsl.select().from(CASH_REGISTER_SESSION_STATUS)
}
