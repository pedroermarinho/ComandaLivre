package io.github.pedroermarinho.company.domain.usecases.company

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.CompanyTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyTypeRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyTypeMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchTypeCompanyUseCase(
    private val companyTypeRepository: CompanyTypeRepository,
    private val companyTypeMapper: CompanyTypeMapper,
) {
    fun getAll(pageable: PageableDTO): Result<PageDTO<CompanyTypeDTO>> = companyTypeRepository.getAll(pageable).map { it.map { entity -> companyTypeMapper.toDTO(entity) } }

    fun getAll(): Result<List<CompanyTypeDTO>> = companyTypeRepository.getAll().map { it.map { entity -> companyTypeMapper.toDTO(entity) } }

    fun getById(id: Int): Result<CompanyTypeDTO> = companyTypeRepository.getById(id).map { companyTypeMapper.toDTO(it) }

    fun getById(publicId: UUID): Result<CompanyType> = companyTypeRepository.getById(publicId).map { it }

    fun getByKey(key: String): Result<CompanyType> = companyTypeRepository.getByKey(key)

    fun getByEnum(enum: CompanyTypeEnum): Result<CompanyType> = getByKey(enum.value)
}
