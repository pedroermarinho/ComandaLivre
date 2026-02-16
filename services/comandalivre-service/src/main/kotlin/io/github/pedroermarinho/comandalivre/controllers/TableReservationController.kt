package io.github.pedroermarinho.comandalivre.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table.CreateTableReservationRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table.UpdateTableReservationStatusRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.table.TableReservationResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.CreateTableReservationUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.GetTableReservationsUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.UpdateTableReservationStatusUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/v1/comandalivre/table-reservations")
@Tag(name = "Reservas de Mesa", description = "Endpoints para gerenciamento de reservas de mesa do sistema.")
class TableReservationController(
    private val createTableReservationUseCase: CreateTableReservationUseCase,
    private val updateTableReservationStatusUseCase: UpdateTableReservationStatusUseCase,
    private val getTableReservationsUseCase: GetTableReservationsUseCase,
) {
    @Operation(
        summary = "Criar nova reserva de mesa",
        description = "Cria uma nova reserva de mesa para um cliente ou usuário específico.",
    )
    @PostMapping
    fun create(
        @Parameter(description = "Dados para criação da reserva de mesa.", required = true)
        @Valid
        @RequestBody form: CreateTableReservationRequest,
        uriBuilder: UriComponentsBuilder,
    ): ResponseEntity<TableReservationResponse> {
        // TODO: implementação mock
        throw NotImplementedError("Função não implementada")
//        val result = createTableReservationUseCase.execute(form).getOrThrow()
//        val uri = uriBuilder.path("/api/v1/table-reservations/{publicId}").buildAndExpand(result.id).toUri()
//        return ResponseEntity.created(uri).body(result)
    }

    @Operation(
        summary = "Atualizar status da reserva de mesa",
        description = "Atualiza o status de uma reserva de mesa existente.",
    )
    @PatchMapping("/{publicId}/status")
    fun updateStatus(
        @Parameter(description = "ID público da reserva de mesa.", required = true)
        @PathVariable publicId: UUID,
        @Parameter(description = "Novo status para a reserva.", required = true)
        @Valid
        @RequestBody form: UpdateTableReservationStatusRequest,
    ): ResponseEntity<TableReservationResponse> {
        throw NotImplementedError("Função não implementada")
        // TODO: implementação mock
//        val result = updateTableReservationStatusUseCase.execute(publicId, form).getOrThrow()
//        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "Buscar reserva de mesa por ID",
        description = "Retorna os detalhes de uma reserva de mesa específica pelo seu ID público.",
    )
    @GetMapping("/{publicId}")
    fun getByPublicId(
        @Parameter(description = "ID público da reserva de mesa.", required = true)
        @PathVariable publicId: UUID,
    ): ResponseEntity<TableReservationResponse> {
        throw NotImplementedError("Função não implementada")
        // TODO: implementação mock
//        val result = getTableReservationsUseCase.getByPublicId(publicId).getOrThrow()
//        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "Buscar reservas de mesa por período e mesa",
        description = "Retorna as reservas de mesa para um período e mesa específicos.",
    )
    @GetMapping("/by-table/{tableId}")
    fun findByTableIdAndPeriod(
        @Parameter(description = "ID interno da mesa.", required = true)
        @PathVariable tableId: Int,
        @Parameter(description = "Data e hora de início do período.", required = true)
        @RequestParam start: LocalDateTime,
        @Parameter(description = "Data e hora de término do período.", required = true)
        @RequestParam end: LocalDateTime,
    ): ResponseEntity<List<TableReservationResponse>> {
        throw NotImplementedError("Função não implementada")
        // TODO: implementação mock
//        val result = getTableReservationsUseCase.findByTableIdAndPeriod(tableId, start, end).getOrThrow()
//        return ResponseEntity.ok(result)
    }
}
