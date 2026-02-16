package io.github.pedroermarinho.comandalivre.domain.usecases.table

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableReservationRepository
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Transactional(readOnly = true)
@UseCase
class GetTableReservationsUseCase(
    private val tableReservationRepository: TableReservationRepository,
) {
    fun getByPublicId(publicId: UUID): Any = tableReservationRepository.getById(publicId).getOrThrow()

    fun findByTableIdAndPeriod(
        tableId: Int,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Any = tableReservationRepository.getByTableIdAndPeriod(tableId, start, end).getOrThrow()
}
