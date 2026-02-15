package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.cashregister

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ClosingEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.SessionStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ClosingRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.SessionRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.cashregister.CloseSessionRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.SearchCommandUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@UseCase
class CloseSessionUseCase(
    private val sessionRepository: SessionRepository,
    private val closingRepository: ClosingRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchSessionStatusUseCase: SearchSessionStatusUseCase,
    private val searchSessionUseCase: SearchSessionUseCase,
    private val searchCommandUseCase: SearchCommandUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun execute(form: CloseSessionRequest): Result<Unit> =
        runCatching {
            log.info { "Iniciando fechamento de sessão para a empresa ID: ${form.companyId}" }

            val currentUserId = currentUserUseCase.getUserId().getOrThrow()
            val companyInternalId = searchCompanyUseCase.getIdById(form.companyId).getOrThrow()
            val session = searchSessionUseCase.getActiveByCompanyId(companyInternalId).getOrThrow()
            val employee = searchEmployeeUseCase.getByUserIdAndCompanyId(currentUserId, companyInternalId).getOrThrow()
            val status = searchSessionStatusUseCase.getByEnum(SessionStatusEnum.CLOSED).getOrThrow()

            val commands =
                searchCommandUseCase
                    .getAllList(
                        companyId = companyInternalId,
                        startDate = session.startedAt,
                        endDate = LocalDateTime.now(),
                        status = CommandStatusEnum.CLOSED,
                    ).getOrThrow()

            closingRepository
                .save(
                    ClosingEntity.createNew(
                        sessionId = session.id.internalId,
                        employeeId = employee.id.internalId,
                        countedCash = form.countedCash,
                        countedCard = form.countedCard,
                        countedPix = form.countedPix,
                        countedOthers = form.countedOthers,
                        observations = form.observations,
                        initialValue = session.initialValue,
                        commands = commands,
                    ),
                ).getOrThrow()

            sessionRepository
                .save(
                    session.updateStatus(
                        status = status,
                        closedByUserId = currentUserId,
                    ),
                ).getOrThrow()

            log.info { "Sessão ID: ${session.id} para a empresa ID: ${form.companyId} finalizada com sucesso" }
        }
}
