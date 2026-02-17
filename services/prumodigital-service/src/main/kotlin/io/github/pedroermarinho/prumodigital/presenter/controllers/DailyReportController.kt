package io.github.pedroermarinho.prumodigital.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyreport.DailyReportRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyreport.DailyReportResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport.CreateDailyReportUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport.SearchDailyReportUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport.UpdateDailyReportUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyReportMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/prumodigital/daily-reports")
@Tag(name = "Relatórios Diários", description = "Endpoints para gerenciar relatórios diários de projetos")
class DailyReportController(
    private val createDailyReportUseCase: CreateDailyReportUseCase,
    private val searchDailyReportUseCase: SearchDailyReportUseCase,
    private val updateDailyReportUseCase: UpdateDailyReportUseCase,
    private val dailyReportMapper: DailyReportMapper,
) {
    @PostMapping
    @Operation(summary = "Cria um novo relatório diário")
    fun create(
        @RequestBody @Valid form: DailyReportRequest,
    ): ResponseEntity<Unit> {
        createDailyReportUseCase.execute(form).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Lista relatórios diários por ID do projeto")
    fun getByProjectId(
        @PathVariable projectId: UUID,
        @ParameterObject pageable: PageableDTO,
    ): ResponseEntity<PageDTO<DailyReportResponse>> {
        val result = searchDailyReportUseCase.getByProjectId(projectId, pageable).getOrThrow()
        return ResponseEntity.ok(result.map { dailyReportMapper.toResponse(it) })
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um relatório diário por ID")
    fun getById(
        @PathVariable id: UUID,
    ): ResponseEntity<DailyReportResponse> {
        val result = searchDailyReportUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(dailyReportMapper.toResponse(result))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um relatório diário existente")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid form: DailyReportRequest,
    ): ResponseEntity<Unit> {
        updateDailyReportUseCase.execute(id, form).getOrThrow()
        return ResponseEntity.ok().build()
    }
}
