package io.github.pedroermarinho.prumodigital.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyactivity.DailyActivityStatusResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyactivity.SearchDailyActivityStatusUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyActivityStatusMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/prumodigital/daily-activity-status")
@Tag(name = "Status de Atividades Diárias", description = "Endpoints para gerenciar os status das atividades diárias")
class DailyActivityStatusController(
    private val searchDailyActivityStatusUseCase: SearchDailyActivityStatusUseCase,
    private val dailyActivityStatusMapper: DailyActivityStatusMapper,
) {
    @GetMapping
    @Operation(summary = "Lista todos os status de atividades diárias")
    fun getAll(): ResponseEntity<List<DailyActivityStatusResponse>> {
        val result = searchDailyActivityStatusUseCase.getAll().getOrThrow()
        return ResponseEntity.ok(result.map { dailyActivityStatusMapper.toResponse(it) })
    }
}
