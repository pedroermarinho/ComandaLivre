package io.github.pedroermarinho.prumodigital.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.WeatherStatusEntity
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface WeatherStatusRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<WeatherStatusEntity>>

    fun getAll(): Result<List<WeatherStatusEntity>>

    fun getById(id: UUID): Result<WeatherStatusEntity>

    fun getById(id: Int): Result<WeatherStatusEntity>

    fun getByKey(key: String): Result<WeatherStatusEntity>

    fun save(entity: WeatherStatusEntity): Result<EntityId>
}
