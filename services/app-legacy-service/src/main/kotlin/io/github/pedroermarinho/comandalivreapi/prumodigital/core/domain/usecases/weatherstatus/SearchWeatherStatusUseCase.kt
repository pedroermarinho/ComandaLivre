package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.weatherstatus

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.WeatherStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.WeatherStatusRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.WeatherStatusMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchWeatherStatusUseCase(
    private val weatherStatusRepository: WeatherStatusRepository,
    private val weatherStatusMapper: WeatherStatusMapper,
) {
    fun getAll(pageable: PageableDTO): Result<PageDTO<WeatherStatusDTO>> =
        runCatching {
            weatherStatusRepository
                .getAll(pageable)
                .map { page ->
                    page.map { entity -> weatherStatusMapper.toDTO(entity) }
                }.getOrThrow()
        }

    fun getAll(): Result<List<WeatherStatusDTO>> =
        runCatching {
            weatherStatusRepository.getAll().getOrThrow().map { entity -> weatherStatusMapper.toDTO(entity) }
        }

    fun getById(id: UUID): Result<WeatherStatusDTO> =
        runCatching {
            weatherStatusRepository.getById(id).getOrThrow().let { weatherStatusMapper.toDTO(it) }
        }

    fun getById(id: Int): Result<WeatherStatusDTO> =
        runCatching {
            weatherStatusRepository.getById(id).getOrThrow().let { weatherStatusMapper.toDTO(it) }
        }

    fun getByKey(key: String): Result<WeatherStatusDTO> =
        runCatching {
            weatherStatusRepository.getByKey(key).getOrThrow().let { weatherStatusMapper.toDTO(it) }
        }
}
