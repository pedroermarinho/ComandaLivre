package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyactivity

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyActivityStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyActivityStatusRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyActivityStatusMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchDailyActivityStatusUseCase(
    private val dailyActivityStatusRepository: DailyActivityStatusRepository,
    private val dailyActivityStatusMapper: DailyActivityStatusMapper,
) {
    fun getAll(pageable: PageableDTO): Result<PageDTO<DailyActivityStatusDTO>> =
        runCatching {
            dailyActivityStatusRepository
                .getAll(pageable)
                .map { page ->
                    page.map { dailyActivityStatusMapper.toDTO(it) }
                }.getOrThrow()
        }

    fun getAll(): Result<List<DailyActivityStatusDTO>> =
        runCatching {
            dailyActivityStatusRepository.getAll().getOrThrow().map { dailyActivityStatusMapper.toDTO(it) }
        }

    fun getById(id: UUID): Result<DailyActivityStatusDTO> =
        runCatching {
            dailyActivityStatusRepository.getById(id).getOrThrow().let { dailyActivityStatusMapper.toDTO(it) }
        }

    fun getById(id: Int): Result<DailyActivityStatusDTO> =
        runCatching {
            dailyActivityStatusRepository.getById(id).getOrThrow().let { dailyActivityStatusMapper.toDTO(it) }
        }

    fun getByKey(key: String): Result<DailyActivityStatusDTO> =
        runCatching {
            dailyActivityStatusRepository.getByKey(key).getOrThrow().let { dailyActivityStatusMapper.toDTO(it) }
        }
}
