package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.table.TableDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.TableEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.TableMapper
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchTableUseCase(
    private val tableRepository: TableRepository,
    private val searchTableStatusUseCase: SearchTableStatusUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val tableMapper: TableMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getAll(
        pageable: PageableDTO,
        companyPublicId: UUID,
    ): Result<PageDTO<TableDTO>> =
        runCatching {
            val companyId = searchCompanyUseCase.getIdById(companyPublicId).getOrThrow()
            tableRepository.getAll(pageable, companyId).map { page -> page.map { convert(it).getOrThrow() } }.getOrThrow()
        }

    fun getAllList(companyPublicId: UUID): Result<List<TableDTO>> =
        runCatching {
            val companyId = searchCompanyUseCase.getIdById(companyPublicId).getOrThrow()
            tableRepository
                .getAllList(companyId)
                .map {
                    it
                        .sortedBy { item -> item.name.value.toIntOrNull() ?: Int.MAX_VALUE }
                        .map { item -> convert(item).getOrThrow() }
                }.getOrThrow()
        }

    fun getById(id: UUID): Result<TableDTO> =
        runCatching {
            tableRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun getEntityById(id: UUID): Result<TableEntity> =
        runCatching {
            tableRepository.getById(id).getOrThrow()
        }

    fun getById(id: Int): Result<TableDTO> =
        runCatching {
            tableRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun getByIdUnsafe(id: Int): Result<TableDTO> =
        runCatching {
            tableRepository.getByIdUnsafe(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun existsById(id: UUID): Boolean = tableRepository.existsById(id)

    fun checkExistsById(id: UUID): Result<Unit> {
        if (!existsById(id)) return Result.failure(NotFoundException("Mesa não encontrada com o ID: $id"))

        return Result.success(Unit)
    }

    fun existsByNameAndCompanyId(
        name: String,
        companyId: Int,
    ): Boolean = tableRepository.existsByNameAndCompanyId(name, companyId)

    fun checkExistsByNameAndCompanyId(
        name: String,
        companyId: Int,
    ): Result<Unit> {
        if (existsByNameAndCompanyId(name, companyId)) {
            return Result.failure(NotFoundException("Já existe uma mesa com o nome '$name'"))
        }
        return Result.success(Unit)
    }

    private fun convert(entity: TableEntity): Result<TableDTO> =
        runCatching {
            tableMapper.toDTO(
                entity = entity,
            )
        }.onFailure { log.error(it) { "Erro ao converter TableEntity para TableDTO para o ID da entidade: ${entity.id}" } }
}
