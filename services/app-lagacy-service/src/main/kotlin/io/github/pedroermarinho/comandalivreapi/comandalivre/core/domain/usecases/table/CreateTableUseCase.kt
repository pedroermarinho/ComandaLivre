package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.TableEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.TableStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table.TableBulkCreateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table.TableCreateRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateTableUseCase(
    private val tableRepository: TableRepository,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchTableStatusUseCase: SearchTableStatusUseCase,
    private val searchTableUseCase: SearchTableUseCase,
) {
    fun create(form: TableCreateRequest): Result<EntityId> =
        runCatching {
            val companyId =
                searchCompanyUseCase
                    .getIdById(form.companyId)
                    .getOrThrow()
            val status =
                searchTableStatusUseCase
                    .getByEnum(TableStatusEnum.AVAILABLE)
                    .getOrThrow()

            searchTableUseCase
                .checkExistsByNameAndCompanyId(
                    name = form.name,
                    companyId = companyId,
                ).getOrThrow()
            val table =
                TableEntity.createNew(
                    publicId = form.publicId,
                    name = form.name,
                    numPeople = form.numPeople,
                    description = form.description,
                    companyId = companyId,
                    status = status,
                )
            tableRepository.save(table).getOrThrow()
        }

    fun create(form: TableBulkCreateRequest): Result<Unit> =
        runCatching {
            val companyId = searchCompanyUseCase.getIdById(form.companyId).getOrThrow()
            val status = searchTableStatusUseCase.getByEnum(TableStatusEnum.AVAILABLE).getOrThrow()
            for (i in form.start..form.end) {
                val name = i.toString()
                val exists = searchTableUseCase.existsByNameAndCompanyId(name, companyId)
                if (!exists) {
                    val table =
                        TableEntity.createNew(
                            name = name,
                            numPeople = form.numPeople,
                            description = null,
                            companyId = companyId,
                            status = status,
                        )
                    tableRepository.save(table)
                }
            }
        }
}
