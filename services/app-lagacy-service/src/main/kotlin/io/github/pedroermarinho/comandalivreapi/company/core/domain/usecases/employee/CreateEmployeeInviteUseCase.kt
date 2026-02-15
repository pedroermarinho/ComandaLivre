package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import com.github.f4b6a3.uuid.UuidCreator
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeInviteEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.EmployeeInviteEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.employee.EmployeeInviteRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.EmployeeInviteCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional
@UseCase
class CreateEmployeeInviteUseCase(
    private val employeeInviteRepository: EmployeeInviteRepository,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchRoleTypeUseCase: SearchRoleTypeUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val eventPublisher: ApplicationEventPublisher,
    private val searchEmployeeInviteStatusUseCase: SearchEmployeeInviteStatusUseCase,
    private val searchEmployeeInviteUseCase: SearchEmployeeInviteUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun create(form: EmployeeInviteRequest): Result<EntityId> =
        runCatching {
            val company = searchCompanyUseCase.getById(form.companyId).getOrThrow()

            val roleType = searchRoleTypeUseCase.getById(form.roleId).getOrThrow()

            val userResult = searchUserUseCase.getByEmail(form.email)

            val status = searchEmployeeInviteStatusUseCase.getByEnum(EmployeeInviteEnum.PENDING).getOrThrow()

            userResult.onSuccess {
                searchEmployeeUseCase.checkEmployeeOfCompany(it.id.internalId, company.id.internalId).getOrThrow()
            }

            val user = userResult.getOrNull()

            val employeeInvite =
                EmployeeInviteEntity.createNew(
                    userId = user?.id?.internalId,
                    companyId = company.id.internalId,
                    role = roleType,
                    status = status,
                    email = form.email,
                    expirationDate = LocalDate.now().plusDays(30),
                    token = UuidCreator.getTimeOrderedEpoch(),
                )
            val createdInviteId = employeeInviteRepository.save(employeeInvite).getOrThrow()

            val createdInvite = searchEmployeeInviteUseCase.getById(createdInviteId.internalId).getOrThrow()

            eventPublisher.publishEvent(
                EmployeeInviteCreatedEvent(
                    recipientEmail = form.email,
                    companyName = company.name,
                    roleName = roleType.name.value,
                    inviteToken = createdInvite.token,
                    userId = user?.id?.internalId,
                ),
            )

            createdInviteId
        }
}
