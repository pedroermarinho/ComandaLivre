package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserFilterDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserRegistrationsPerDayDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.UserEntity
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository {
    fun getAll(
        pageable: PageableDTO,
        filter: UserFilterDTO,
    ): Result<PageDTO<UserEntity>>

    fun getBySub(sub: String): Result<UserEntity>

    fun getByEmail(email: String): Result<UserEntity>

    fun existsBySub(sub: String): Boolean

    fun getById(id: Int): Result<UserEntity>

    fun getById(id: UUID): Result<UserEntity>

    fun getIdBySub(sub: String): Result<Int>

    fun count(): Result<Long>

    fun getUserRegistrationsLastDays(days: Long): Result<List<UserRegistrationsPerDayDTO>>

    fun getIdByPublicId(id: UUID): Result<EntityId>

    fun save(entity: UserEntity): Result<EntityId>
}
