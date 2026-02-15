package io.github.pedroermarinho.comandalivreapi.shared.core.data.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.AddressEntity
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.AddressRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.AddressPersistenceMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.toEntity
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import shared.tables.references.ADDRESSES

@Repository
class JooqAddressRepository(
    private val dsl: DSLContext,
    private val addressPersistenceMapper: AddressPersistenceMapper,
) : AddressRepository {
    override fun getById(id: Int): Result<AddressEntity> {
        val result =
            dsl
                .select()
                .from(ADDRESSES)
                .where(ADDRESSES.ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Usuário não encontrado"))
        return addressPersistenceMapper.toEntity(result.into(ADDRESSES))
    }

    override fun getById(id: java.util.UUID): Result<AddressEntity> {
        val result =
            dsl
                .select()
                .from(ADDRESSES)
                .where(ADDRESSES.PUBLIC_ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Endereço não encontrado"))
        return addressPersistenceMapper.toEntity(result.into(ADDRESSES))
    }

    override fun save(entity: AddressEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = addressPersistenceMapper.toRecord(entity).getOrThrow()

            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(ADDRESSES)
                        .set(record)
                        .where(ADDRESSES.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o endereço")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }

            val result =
                dsl
                    .insertInto(ADDRESSES)
                    .set(record)
                    .returning(ADDRESSES.ID, ADDRESSES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o endereço")

            EntityId(result.id!!, result.publicId)
        }
}
