package io.github.pedroermarinho.company.domain.usecases.employee

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.EmployeeInviteEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.employee.EmployeeForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteRepository
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.user.domain.usecases.user.SearchUserUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class ChangeStatusEmployeeInviteUseCase(
    private val employeeInviteRepository: EmployeeInviteRepository,
    private val searchEmployeeInviteUseCase: SearchEmployeeInviteUseCase,
    private val currentUserService: CurrentUserService,
    private val createEmployeeUseCase: CreateEmployeeUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val searchEmployeeInviteStatusUseCase: SearchEmployeeInviteStatusUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun changeStatus(
        publicId: UUID,
        status: EmployeeInviteEnum,
    ): Result<Unit> =
        runCatching {
            val employeeInvite = employeeInviteRepository.getById(publicId).getOrThrow()
            val user = currentUserService.getLoggedUser().getOrThrow()

            if (employeeInvite.email.value != user.email) {
                return Result.failure(BusinessLogicException("Usuário não autorizado a alterar este EmployeeInvite"))
            }

            val newStatus = searchEmployeeInviteStatusUseCase.getByEnum(status).getOrThrow()

            employeeInviteRepository.save(employeeInvite.updateStatus(newStatus)).getOrThrow()

            val userId =
                employeeInvite.userId?.value ?: searchUserUseCase
                    .getByEmail(employeeInvite.email.value)
                    .getOrThrow()
                    .id.internalId

            if (status == EmployeeInviteEnum.ACCEPTED) {
                createEmployeeUseCase
                    .create(
                        EmployeeForm(
                            companyId = employeeInvite.companyId.value,
                            userId = userId,
                            roleId = employeeInvite.role.id.internalId,
                        ),
                    ).getOrThrow()
            }

            return Result.success(Unit)
        }
}
