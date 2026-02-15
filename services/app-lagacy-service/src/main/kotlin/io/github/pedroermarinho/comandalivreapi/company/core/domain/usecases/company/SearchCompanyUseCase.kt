package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyCountByTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyRepository
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchCompanyUseCase(
    private val companyRepository: CompanyRepository,
    private val companyMapper: CompanyMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getAll(pageable: PageableDTO): Result<PageDTO<CompanyDTO>> =
        runCatching {
            companyRepository
                .getAll(pageable)
                .map { page -> page.map { companyMapper.toDTO(it).getOrThrow() } }
                .getOrThrow()
        }

    fun getById(companyId: Int): Result<CompanyDTO> {
        return runCatching {
            return companyRepository
                .getById(companyId)
                .map { companyMapper.toDTO(it).getOrThrow() }
        }
    }

    fun getById(publicId: UUID): Result<CompanyDTO> {
        return runCatching {
            return companyRepository
                .getById(publicId)
                .map { companyMapper.toDTO(it).getOrThrow() }
        }
    }

    fun getByDomain(domain: String): Result<CompanyDTO> {
        return runCatching {
            return companyRepository
                .getByDomain(domain)
                .map { companyMapper.toDTO(it).getOrThrow() }
        }
    }

    fun getIdById(companyId: UUID): Result<Int> = companyRepository.getPrivateIdByPublicId(companyId)

    fun getSettingsIdByCompanyId(companyId: UUID): Result<Int> = companyRepository.getSettingsIdByPublicId(companyId)

    fun count(): Result<Long> = companyRepository.count()

    fun countByType(): Result<List<CompanyCountByTypeDTO>> = companyRepository.countByType()

    fun existDomain(domain: String): Boolean = companyRepository.existDomain(domain)

    fun exists(id: UUID): Boolean = companyRepository.exists(id)

    fun checkExists(id: UUID): Result<Unit> {
        if (exists(id)) {
            return Result.success(Unit)
        }

        log.error { "Empresa com ID $id não encontrada" }
        return Result.failure(NotFoundException("Empresa não encontrada para"))
    }

    fun getAddressIdByCompanyId(companyId: UUID): Result<Int> = companyRepository.getAddressIdByCompanyId(companyId)

    fun existsByName(value: String): Boolean = companyRepository.existsByName(value)

    fun existsByDomain(value: String): Boolean = companyRepository.existDomain(value)
}
