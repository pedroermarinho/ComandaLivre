package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.cashregister

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister.ClosingDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ClosingEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ClosingRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ClosingMapper
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchClosingUseCase(
    private val closingRepository: ClosingRepository,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchSessionUseCase: SearchSessionUseCase,
    private val closingMapper: ClosingMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getBySessionId(sessionId: UUID): Result<ClosingDTO> =
        runCatching {
            log.info { "uma alteração qualquer sdfsdfsdasdasdasdasdasdasdasdas  asdasdasd" }
            val session = searchSessionUseCase.getById(sessionId).getOrThrow()
            closingRepository
                .getBySessionId(session.id.internalId)
                .map { convert(it).getOrThrow() }
                .getOrThrow()
        }

    fun getById(id: Int): Result<ClosingDTO> =
        runCatching {
            closingRepository
                .getById(id)
                .map { convert(it).getOrThrow() }
                .getOrThrow()
        }

    private fun convert(entity: ClosingEntity): Result<ClosingDTO> =
        runCatching {
            closingMapper.toDTO(
                entity = entity,
                employee = searchEmployeeUseCase.getById(entity.employeeId).getOrThrow(),
                session = searchSessionUseCase.getById(entity.sessionId).getOrThrow(),
            )
        }.onFailure { log.error(it) { "Erro ao converter ProductEntity para ProductDTO para o ID da entidade: ${entity.id}" } }
}
