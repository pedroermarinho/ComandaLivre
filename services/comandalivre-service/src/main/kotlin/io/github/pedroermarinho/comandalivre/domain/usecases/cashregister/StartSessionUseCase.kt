package io.github.pedroermarinho.comandalivre.domain.usecases.cashregister

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.SessionEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.SessionStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.SessionRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.cashregister.StartSessionRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.user.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class StartSessionUseCase(
    private val sessionRepository: SessionRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchSessionStatusUseCase: SearchSessionStatusUseCase,
    private val searchSessionUseCase: SearchSessionUseCase,
) {
    @Transactional
    fun execute(form: StartSessionRequest): Result<EntityId> =
        runCatching {
            val currentUserId = currentUserUseCase.getUserId().getOrThrow()
            val companyInternalId = searchCompanyUseCase.getIdById(form.companyId).getOrThrow()
            val employee = searchEmployeeUseCase.getByUserIdAndCompanyId(currentUserId, companyInternalId).getOrThrow()
            val status = searchSessionStatusUseCase.getByEnum(SessionStatusEnum.OPEN).getOrThrow()

            searchSessionUseCase.getActiveByCompanyId(form.companyId).onSuccess {
                throw BusinessLogicException("Já existe uma sessão de caixa aberta para esta empresa.")
            }

            return sessionRepository
                .save(
                    SessionEntity.createNew(
                        employeeId = employee.id.internalId,
                        companyId = companyInternalId,
                        openedByUserId = currentUserId,
                        initialValue = form.initialValue,
                        notes = form.notes,
                        status = status,
                    ),
                )
        }
}
