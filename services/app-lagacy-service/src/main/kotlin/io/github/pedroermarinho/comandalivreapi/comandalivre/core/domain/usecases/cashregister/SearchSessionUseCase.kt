package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.cashregister

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister.SessionDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.SessionEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.SessionStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.SessionRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.SessionMapper
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchSessionUseCase(
    private val sessionRepository: SessionRepository,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchSessionStatusUseCase: SearchSessionStatusUseCase,
    private val sessionMapper: SessionMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getActiveByCompanyId(companyId: UUID): Result<SessionDTO> =
        runCatching {
            val companyInternalId = searchCompanyUseCase.getIdById(companyId).getOrThrow()
            val status = searchSessionStatusUseCase.getByEnum(SessionStatusEnum.OPEN).getOrThrow()

            sessionRepository
                .getByStatus(
                    companyId = companyInternalId,
                    statusId = status.id.internalId,
                ).map { convert(it).getOrThrow() }
                .getOrThrow()
        }

    fun getActiveByCompanyId(companyId: Int): Result<SessionEntity> =
        runCatching {
            val status = searchSessionStatusUseCase.getByEnum(SessionStatusEnum.OPEN).getOrThrow()

            sessionRepository
                .getByStatus(
                    companyId = companyId,
                    statusId = status.id.internalId,
                ).getOrThrow()
        }

    fun getById(id: UUID): Result<SessionDTO> =
        runCatching {
            sessionRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    fun getById(id: Int): Result<SessionDTO> =
        runCatching {
            sessionRepository.getById(id).map { convert(it).getOrThrow() }.getOrThrow()
        }

    private fun convert(entity: SessionEntity): Result<SessionDTO> =
        runCatching {
            val employee = searchEmployeeUseCase.getById(entity.employeeId.value).getOrThrow()
            val openedByUser = entity.openedByUserId?.let { userId -> searchUserUseCase.getById(userId.value).getOrThrow() }
            val closedByUser = entity.closedByUserId?.let { userId -> searchUserUseCase.getById(userId.value).getOrThrow() }

            sessionMapper.toDTO(
                entity = entity,
                employee = employee,
                openedByUser = openedByUser,
                closedByUser = closedByUser,
            )
        }.onFailure { log.error(it) { "Erro ao converter SessionEntity para SessionDTO para o ID da entidade: ${entity.id}" } }
}
