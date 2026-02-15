package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeRepository
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateEmployeeUseCase(
    private val employeeRepository: EmployeeRepository,
) {
    fun changeStatus(
        employeePublicId: UUID,
        status: Boolean,
    ): Result<Unit> =
        runCatching {
            val employee = employeeRepository.getById(employeePublicId).getOrThrow()
            employeeRepository.save(employee.updateStatus(status)).getOrThrow()
        }
}
