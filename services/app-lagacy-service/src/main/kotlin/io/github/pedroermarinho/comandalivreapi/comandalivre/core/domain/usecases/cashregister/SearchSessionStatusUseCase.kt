package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.cashregister

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister.SessionStatusDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.SessionStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.SessionStatusRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.SessionStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.SessionStatusMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class SearchSessionStatusUseCase(
    private val sessionStatusRepository: SessionStatusRepository,
    private val sessionStatusMapper: SessionStatusMapper,
) {
    fun getAll(): Result<List<SessionStatusDTO>> =
        sessionStatusRepository.getAll().map { list ->
            list.map { sessionStatusMapper.toDTO(it) }
        }

    fun getByKey(key: String): Result<SessionStatus> = sessionStatusRepository.getByKey(key)

    fun getById(id: Int): Result<SessionStatusDTO> = sessionStatusRepository.getById(id).map { sessionStatusMapper.toDTO(it) }

    fun getByEnum(status: SessionStatusEnum): Result<SessionStatus> = this.getByKey(status.value)
}
