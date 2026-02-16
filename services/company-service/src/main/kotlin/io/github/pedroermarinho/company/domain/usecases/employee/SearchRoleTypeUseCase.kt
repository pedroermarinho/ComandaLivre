package io.github.pedroermarinho.company.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.RoleTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.RoleTypeRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.RoleTypeMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchRoleTypeUseCase(
    private val roleTypeRepository: RoleTypeRepository,
    private val roleTypeMapper: RoleTypeMapper,
) {
    fun getByEnum(roleType: RoleTypeEnum): Result<RoleTypeDTO> = roleTypeRepository.getByKey(roleType.value).map { roleTypeMapper.toDTO(it) }

    fun getById(id: Int): Result<RoleType> = roleTypeRepository.getById(id)

    fun getById(id: UUID): Result<RoleType> = roleTypeRepository.getById(id)

    fun getAll(pageable: PageableDTO): Result<PageDTO<RoleTypeDTO>> = roleTypeRepository.getAll(pageable).map { it.map { entity -> roleTypeMapper.toDTO(entity) } }

    fun getAll(): Result<List<RoleTypeDTO>> = roleTypeRepository.getAll().map { it.map { entity -> roleTypeMapper.toDTO(entity) } }
}
