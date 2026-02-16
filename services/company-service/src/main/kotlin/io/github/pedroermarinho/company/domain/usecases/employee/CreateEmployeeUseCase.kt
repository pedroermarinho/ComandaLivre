package io.github.pedroermarinho.company.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.employee.EmployeeForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeRepository
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateEmployeeUseCase(
    private val employeeRepository: EmployeeRepository,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchRoleTypeUseCase: SearchRoleTypeUseCase,
) {
    fun create(form: EmployeeForm): Result<EntityId> =
        runCatching {
            searchEmployeeUseCase
                .checkEmployeeOfCompany(
                    userId = form.userId,
                    companyId = form.companyId,
                ).getOrThrow()

            val role = searchRoleTypeUseCase.getById(form.roleId).getOrThrow()
            val employee =
                EmployeeEntity.createNew(
                    role = role,
                    companyId = form.companyId,
                    userId = form.userId,
                    status = true,
                )
            employeeRepository.save(employee).getOrThrow()
        }
}
