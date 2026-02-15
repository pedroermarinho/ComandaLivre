package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project

import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ProjectEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.enums.ProjectStatusEnum
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.ProjectRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.project.ProjectCreateRequest
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address.CreateAddressUseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val searchProjectStatusUseCase: SearchProjectStatusUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val createAddressUseCase: CreateAddressUseCase,
) {
    fun execute(form: ProjectCreateRequest): Result<EntityId> =
        runCatching {
            val companyId = searchCompanyUseCase.getIdById(form.companyId).getOrThrow()

            if (searchProjectUseCase.existsByCodeAndCompanyId(form.code, companyId)) {
                throw BusinessLogicException("Projeto com código '${form.code}' já existe para esta empresa.")
            }

            val initialStatus = searchProjectStatusUseCase.getByEnum(ProjectStatusEnum.PLANNING).getOrThrow()

            val addressId = form.address?.let { createAddressUseCase.create(it).getOrThrow() }

            val project =
                ProjectEntity.createNew(
                    name = form.name,
                    code = form.code,
                    clientName = form.clientName,
                    budget = form.budget,
                    plannedStartDate = form.startDatePlanned,
                    plannedEndDate = form.endDatePlanned,
                    companyId = companyId,
                    addressId = addressId?.internalId,
                    projectStatusId = initialStatus.id.internalId,
                )
            projectRepository.save(project).getOrThrow()
        }
}
