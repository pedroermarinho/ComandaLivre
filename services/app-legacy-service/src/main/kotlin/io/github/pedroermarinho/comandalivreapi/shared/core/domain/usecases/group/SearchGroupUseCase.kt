package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.group.GroupDTO
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.GroupEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.GroupRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.GroupMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.toDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchGroupUseCase(
    private val groupRepository: GroupRepository,
    private val groupMapper: GroupMapper,
) {
    fun getAll(pageable: PageableDTO): Result<PageDTO<GroupDTO>> = groupRepository.getAll(pageable).map { it.map { entity -> groupMapper.toDTO(entity) } }

    fun getByKey(key: String): Result<GroupDTO> = groupRepository.getByKey(key).map { groupMapper.toDTO(it) }

    fun getById(id: UUID): Result<GroupDTO> = groupRepository.getById(id).map { groupMapper.toDTO(it) }

    fun getIdByPublicId(id: UUID): Result<EntityId> = groupRepository.getIdByPublicId(id)

    fun getById(id: Int): Result<GroupDTO> = groupRepository.getById(id).map { groupMapper.toDTO(it) }

    fun getByEnum(enum: GroupEnum): Result<GroupDTO> = getByKey(enum.value)
}
