package io.github.pedroermarinho.comandalivre.domain.usecases.table

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableReservationRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table.CreateTableReservationRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.services.DashboardNotifierService
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateTableReservationUseCase(
    private val tableReservationRepository: TableReservationRepository,
    private val tableRepository: TableRepository,
    private val dashboardNotifierService: DashboardNotifierService,
) {
    // TODO: implementar logica de criacao de reserva de mesa
    fun execute(form: CreateTableReservationRequest): Any {
        // Lógica para criar a reserva
//        val reservation = tableReservationRepository.create(form).getOrThrow()

        // Opcional: Atualizar o status da mesa para 'RESERVADA' se a reserva for confirmada
        // Isso dependeria da lógica de negócio e do status da reserva
        // val table = tableRepository.updateStatus(form.tablePublicId, UpdateTableStatusForm(statusKey = "RESERVADA")).getOrThrow()

        // Notificar o dashboard
//        dashboardNotifierService.notifyDashboardUpdate("table_reservations", reservation)

//        return reservation
        return Any()
    }
}
