package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.CASH_REGISTER_SESSIONS
import comandalivre.tables.references.CASH_REGISTER_SESSION_STATUS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.SessionEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.SessionRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.SessionPersistenceMapper
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JooqSessionRepository(
    private val dsl: DSLContext,
    private val sessionPersistenceMapper: SessionPersistenceMapper,
) : SessionRepository {
    override fun getByStatus(
        companyId: Int,
        statusId: Int,
    ): Result<SessionEntity> {
        val record =
            query()
                .where(CASH_REGISTER_SESSIONS.COMPANY_ID.eq(companyId))
                .and(CASH_REGISTER_SESSIONS.STATUS_ID.eq(statusId))
                .and(CASH_REGISTER_SESSIONS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Nenhuma sessão de caixa ativa encontrada"))
        return sessionPersistenceMapper.toEntity(
            cashRegisterSessionsRecord = record.into(CASH_REGISTER_SESSIONS),
            cashRegisterSessionStatusRecord = record.into(CASH_REGISTER_SESSION_STATUS),
        )
    }

    override fun getById(id: Int): Result<SessionEntity> {
        val record =
            query()
                .where(CASH_REGISTER_SESSIONS.ID.eq(id))
                .and(CASH_REGISTER_SESSIONS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Sessão de caixa não encontrada"))

        return sessionPersistenceMapper.toEntity(
            cashRegisterSessionsRecord = record.into(CASH_REGISTER_SESSIONS),
            cashRegisterSessionStatusRecord = record.into(CASH_REGISTER_SESSION_STATUS),
        )
    }

    override fun getById(id: UUID): Result<SessionEntity> {
        val record =
            query()
                .where(CASH_REGISTER_SESSIONS.PUBLIC_ID.eq(id))
                .and(CASH_REGISTER_SESSIONS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Sessão de caixa não encontrada"))

        return sessionPersistenceMapper.toEntity(
            cashRegisterSessionsRecord = record.into(CASH_REGISTER_SESSIONS),
            cashRegisterSessionStatusRecord = record.into(CASH_REGISTER_SESSION_STATUS),
        )
    }

    override fun save(entity: SessionEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = sessionPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(CASH_REGISTER_SESSIONS)
                        .set(record)
                        .where(CASH_REGISTER_SESSIONS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a sessão")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(CASH_REGISTER_SESSIONS)
                    .set(record)
                    .returning(CASH_REGISTER_SESSIONS.ID, CASH_REGISTER_SESSIONS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a sessão")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    private fun query() =
        dsl
            .select()
            .from(CASH_REGISTER_SESSIONS)
            .innerJoin(CASH_REGISTER_SESSION_STATUS)
            .on(CASH_REGISTER_SESSIONS.STATUS_ID.eq(CASH_REGISTER_SESSION_STATUS.ID))
}
