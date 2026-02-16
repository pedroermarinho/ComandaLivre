package io.github.pedroermarinho.comandalivre.domain.usecases.table

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableReservationRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table.UpdateTableReservationStatusRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.services.DashboardNotifierService
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@UseCase
class UpdateTableReservationStatusUseCase(
    private val tableReservationRepository: TableReservationRepository,
    private val dashboardNotifierService: DashboardNotifierService,
) {
    // TODO: Implementar logica de atualização do status da reserva de mesa
    fun execute(
        publicId: UUID,
        form: UpdateTableReservationStatusRequest,
    ): Any {
//        val updatedReservation = tableReservationRepository.updateStatus(publicId, form).getOrThrow()
//        dashboardNotifierService.notifyDashboardUpdate("table_reservations", updatedReservation)
//        return updatedReservation

        // TODO: Implementar essa função
        throw NotImplementedError("Função não implementada")
    }
}
