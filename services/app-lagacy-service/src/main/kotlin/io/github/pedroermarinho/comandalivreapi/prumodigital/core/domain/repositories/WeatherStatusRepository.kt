package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.WeatherStatusEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

interface WeatherStatusRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<WeatherStatusEntity>>

    fun getAll(): Result<List<WeatherStatusEntity>>

    fun getById(id: UUID): Result<WeatherStatusEntity>

    fun getById(id: Int): Result<WeatherStatusEntity>

    fun getByKey(key: String): Result<WeatherStatusEntity>

    fun save(entity: WeatherStatusEntity): Result<EntityId>
}
