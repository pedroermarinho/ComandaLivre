package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.TABLE_RESERVATIONS
import comandalivre.tables.references.TABLE_RESERVATION_STATUS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.TableReservationEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableReservationRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.TableReservationPersistenceMapper
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class JooqTableReservationRepository(
    private val dsl: DSLContext,
    private val tableReservationPersistenceMapper: TableReservationPersistenceMapper,
) : TableReservationRepository {
    override fun getById(publicId: UUID): Result<TableReservationEntity> =
        runCatching {
            query()
                .where(TABLE_RESERVATIONS.PUBLIC_ID.eq(publicId))
                .fetchOne()
                ?.into(TABLE_RESERVATIONS)
                ?.let {
                    tableReservationPersistenceMapper
                        .toEntity(
                            tableReservationsRecord = it.into(TABLE_RESERVATIONS),
                            tableReservationsStatusRecord = it.into(TABLE_RESERVATION_STATUS),
                        ).getOrThrow()
                }
                ?: throw NotFoundException("Reserva não encontrada.")
        }

    override fun getByTableIdAndPeriod(
        tableId: Int,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Result<List<TableReservationEntity>> =
        runCatching {
            query()
                .where(TABLE_RESERVATIONS.TABLE_ID.eq(tableId))
                .and(TABLE_RESERVATIONS.RESERVATION_START.lessOrEqual(end))
                .and(TABLE_RESERVATIONS.RESERVATION_END.greaterOrEqual(start).or(TABLE_RESERVATIONS.RESERVATION_END.isNull))
                .and(TABLE_RESERVATIONS.DELETED_AT.isNull)
                .fetch()
                .map {
                    tableReservationPersistenceMapper
                        .toEntity(
                            tableReservationsRecord = it.into(TABLE_RESERVATIONS),
                            tableReservationsStatusRecord = it.into(TABLE_RESERVATION_STATUS),
                        ).getOrThrow()
                }
        }

    override fun save(entity: TableReservationEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = tableReservationPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(TABLE_RESERVATIONS)
                        .set(record)
                        .where(TABLE_RESERVATIONS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a reserva de mesa")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(TABLE_RESERVATIONS)
                    .set(record)
                    .returning(TABLE_RESERVATIONS.ID, TABLE_RESERVATIONS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a reserva de mesa")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    private fun query() =
        dsl
            .select()
            .from(TABLE_RESERVATIONS)
            .innerJoin(TABLE_RESERVATION_STATUS)
            .on(TABLE_RESERVATIONS.STATUS_ID.eq(TABLE_RESERVATION_STATUS.ID))
}
