package io.github.pedroermarinho.user.data.repositories

import io.github.pedroermarinho.user.domain.entities.VersionEntity
import io.github.pedroermarinho.user.domain.enums.PlatformEnum
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.user.domain.repositories.VersionRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.user.infra.mappers.VersionPersistenceMapper
import io.github.pedroermarinho.user.infra.mappers.toEntity
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import user.tables.references.APPLICATION_VERSIONS

@Repository
class JooqVersionRepository(
    private val dsl: DSLContext,
    private val versionPersistenceMapper: VersionPersistenceMapper,
) : VersionRepository {
    override fun getLatestByPlatform(platform: PlatformEnum): Result<VersionEntity> =
        runCatching {
            val result =
                dsl
                    .select()
                    .from(APPLICATION_VERSIONS)
                    .where(APPLICATION_VERSIONS.PLATFORM.eq(platform.value))
                    .orderBy(APPLICATION_VERSIONS.CREATED_AT.desc())
                    .limit(1)
                    .fetchOne() ?: throw NotFoundException("Nenhuma versão encontrada para a plataforma ${platform.value}")

            return versionPersistenceMapper.toEntity(result.into(APPLICATION_VERSIONS))
        }

    override fun save(entity: VersionEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = versionPersistenceMapper.toRecord(entity).getOrThrow()

            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(APPLICATION_VERSIONS)
                        .set(record)
                        .where(APPLICATION_VERSIONS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a versão da aplicação")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }

            val result =
                dsl
                    .insertInto(APPLICATION_VERSIONS)
                    .set(record)
                    .returning(APPLICATION_VERSIONS.ID, APPLICATION_VERSIONS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a versão da aplicação")

            EntityId(result.id!!, result.publicId)
        }
}
