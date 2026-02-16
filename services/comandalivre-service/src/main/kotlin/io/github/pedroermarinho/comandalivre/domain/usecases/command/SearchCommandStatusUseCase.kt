package io.github.pedroermarinho.comandalivre.domain.usecases.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandStatusDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandStatusRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.CommandStatusMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class SearchCommandStatusUseCase(
    private val commandStatusRepository: CommandStatusRepository,
    private val commandStatusMapper: CommandStatusMapper,
) {
    fun getAll(pageable: PageableDTO): Result<PageDTO<CommandStatusDTO>> = commandStatusRepository.getAll(pageable).map { it.map { entity -> commandStatusMapper.toDTO(entity) } }

    fun getById(statusId: Int): Result<CommandStatusDTO> = commandStatusRepository.getById(statusId).map { commandStatusMapper.toDTO(it) }

    fun getByName(statusName: String): Result<CommandStatusDTO> = commandStatusRepository.getByName(statusName).map { commandStatusMapper.toDTO(it) }

    fun getByKey(key: String): Result<CommandStatus> = commandStatusRepository.getByKey(key)

    fun getByEnum(status: CommandStatusEnum): Result<CommandStatus> = getByKey(status.value)
}
