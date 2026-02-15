package io.github.pedroermarinho.comandalivreapi.prumodigital.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyattendance.DailyAttendanceBatchRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyattendance.DailyAttendanceRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyattendance.DailyAttendanceResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyattendance.RegisterAttendanceUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyattendance.RegisterBatchAttendanceUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyattendance.SearchAttendanceUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyAttendanceMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/prumodigital/daily-attendances")
@Tag(name = "Presenças Diárias", description = "Endpoints para gerenciar presenças diárias")
class DailyAttendanceController(
    private val registerAttendanceUseCase: RegisterAttendanceUseCase,
    private val searchAttendanceUseCase: SearchAttendanceUseCase,
    private val registerBatchAttendanceUseCase: RegisterBatchAttendanceUseCase,
    private val dailyAttendanceMapper: DailyAttendanceMapper,
) {
    @Operation(summary = "Buscar todas as presenças de um relatório diário", description = "Buscar todas as presenças de um relatório diário")
    @GetMapping
    fun getAll(
        @RequestParam dailyReportId: UUID,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<DailyAttendanceResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchAttendanceUseCase.getAll(pageable, dailyReportId).getOrThrow()
        return ResponseEntity.ok(result.map { dailyAttendanceMapper.toResponse(it) })
    }

    @Operation(summary = "Buscar presença por ID público", description = "Buscar presença por ID público")
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
    ): ResponseEntity<DailyAttendanceResponse> {
        val result = searchAttendanceUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(dailyAttendanceMapper.toResponse(result))
    }

    @PostMapping
    @Operation(summary = "Registra a presença de um funcionário em um relatório diário")
    fun register(
        @RequestBody @Valid form: DailyAttendanceRequest,
    ): ResponseEntity<Unit> {
        registerAttendanceUseCase.execute(form).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @PostMapping("/batch")
    @Operation(summary = "Registra a presença de múltiplos funcionários em lote")
    fun registerBatch(
        @RequestBody @Valid form: DailyAttendanceBatchRequest,
    ): ResponseEntity<Unit> {
        registerBatchAttendanceUseCase.execute(form).getOrThrow()
        return ResponseEntity.ok().build()
    }
}
