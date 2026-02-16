package io.github.pedroermarinho.comandalivre.domain.usecases.table

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.table.TableStatusDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.TableStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableStatusTypeRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.TableStatusMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
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
