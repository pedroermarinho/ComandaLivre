package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.ProjectRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateProjectStatusUseCase(
    private val projectRepository: ProjectRepository,
    private val searchProjectStatusUseCase: SearchProjectStatusUseCase,
) {
    fun execute(
        id: UUID,
        statusId: Int,
    ): Result<Unit> =
        // Changed return type to ProjectEntity
        runCatching {
            val existingProject = projectRepository.getById(id).getOrThrow() // Get the entity directly
            val status = searchProjectStatusUseCase.getById(statusId).getOrThrow()

            projectRepository.updateStatus(id, status.id.internalId).getOrThrow() // Returns ProjectEntity
        }
}
