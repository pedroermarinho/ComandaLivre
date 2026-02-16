package io.github.pedroermarinho.user.domain.usecases.eventlog

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.dtos.eventlog.EventLogDTO
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.repositories.EventLogRepository
import io.github.pedroermarinho.user.infra.mappers.EventLogMapper
import io.github.pedroermarinho.user.infra.mappers.toDTO
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class SearchEventLogUseCase(
    private val eventLogRepository: EventLogRepository,
    private val eventLogMapper: EventLogMapper,
) {
    fun getAll(pageable: PageableDTO): Result<PageDTO<EventLogDTO>> = eventLogRepository.getAll(pageable).map { it.map { entity -> eventLogMapper.toDTO(entity) } }
}
