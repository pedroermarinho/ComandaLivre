package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.table.TableStatusDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.TableStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableStatusTypeRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.TableStatusMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class SearchTableStatusUseCase(
    private val tableStatusTypeRepository: TableStatusTypeRepository,
    private val tableStatusMapper: TableStatusMapper,
) {
    fun getByEnum(status: TableStatusEnum): Result<TableStatus> = tableStatusTypeRepository.getByKey(status.value)

    fun getAll(pageable: PageableDTO): Result<PageDTO<TableStatusDTO>> =
        tableStatusTypeRepository.getAll(pageable).map {
            it.map { entity -> tableStatusMapper.toDTO(entity) }
        }

    fun getById(id: Int) = tableStatusTypeRepository.getById(id).map { tableStatusMapper.toDTO(it) }
}
