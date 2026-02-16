package io.github.pedroermarinho.user.presenter.controllers

import io.github.pedroermarinho.user.domain.annotations.RequirePermissions
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.enums.FeatureEnum
import io.github.pedroermarinho.user.domain.response.eventlog.EventLogResponse
import io.github.pedroermarinho.user.domain.usecases.eventlog.SearchEventLogUseCase
import io.github.pedroermarinho.user.infra.mappers.EventLogMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/shared/event-logs")
@Tag(name = "Log de eventos", description = "Gerenciamento de log de eventos")
class EventLogController(
    private val searchEventLogUseCase: SearchEventLogUseCase,
    private val eventLogMapper: EventLogMapper,
) {
    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Buscar todos os logs de eventos",
        description = "Buscar todos os logs de eventos",
    )
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<EventLogResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchEventLogUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { eventLogMapper.toResponse(it) })
    }
}
