package io.github.pedroermarinho.prumodigital.domain.usecases.project

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.enums.ProjectStatusEnum
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.ProjectRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.project.ProjectCreateRequest
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.usecases.address.CreateAddressUseCase
import io.github.pedroermarinho.user.domain.usecases.address.UpdateAddressUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val searchProjectStatusUseCase: SearchProjectStatusUseCase,
    private val createAddressUseCase: CreateAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
) {
    fun execute(
        id: UUID,
        form: ProjectCreateRequest,
    ): Result<Unit> =
        runCatching {
            val existingProject = projectRepository.getById(id).getOrThrow()
            val status = searchProjectStatusUseCase.getByEnum(ProjectStatusEnum.PLANNING).getOrThrow()

            val addressId =
                if (form.address != null) {
                    if (existingProject.addressId != null) {
                        updateAddressUseCase.update(existingProject.addressId, form.address).getOrThrow()
                        existingProject.addressId
                    } else {
                        createAddressUseCase.create(form.address).getOrThrow().internalId
                    }
                } else {
                    null
                }

            val updatedProject =
                existingProject.update(
                    name = form.name,
                    code = form.code,
                    clientName = form.clientName,
                    budget = form.budget,
                    projectStatusId = status.id.internalId,
                    addressId = addressId,
                    plannedStartDate = form.startDatePlanned,
                    plannedEndDate = form.endDatePlanned,
                    actualStartDate = existingProject.actualStartDate,
                    actualEndDate = existingProject.actualEndDate,
                    description = existingProject.description,
                )
            projectRepository.save(updatedProject).map { Unit }.getOrThrow()
        }
}
