package io.github.pedroermarinho.prumodigital.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyactivity.DailyActivityRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyactivity.MyActivityRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyactivity.DailyActivityResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyactivity.*
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyActivityMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/api/v1/prumodigital/daily-activities")
@Tag(name = "Atividades Diárias", description = "Endpoints para gerenciar atividades diárias de projetos")
class DailyActivityController(
    private val addActivityToDailyReportUseCase: AddActivityToDailyReportUseCase,
    private val updateDailyActivityUseCase: UpdateDailyActivityUseCase,
    private val searchDailyActivityUseCase: SearchDailyActivityUseCase,
    private val deleteDailyActivityUseCase: DeleteDailyActivityUseCase,
    private val logMyDailyActivityUseCase: LogMyDailyActivityUseCase,
    private val dailyActivityMapper: DailyActivityMapper,
) {
    @PostMapping("/my-activity")
    @Operation(
        summary = "Registra uma atividade diária para o usuário logado",
        description =
            "Permite que um colaborador registre sua própria atividade diária de forma simplificada." +
                "O sistema automaticamente encontra ou cria o relatório diário para o dia corrente.",
    )
    fun logMyActivity(
        @RequestBody @Valid form: MyActivityRequest,
    ): ResponseEntity<Unit> {
        logMyDailyActivityUseCase.execute(form).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @GetMapping
    @Operation(
        summary = "Buscar todas as atividades diárias de um relatório diário",
        description = "Retorna todas as atividades diárias de um relatório diário de forma paginada.",
    )
    fun getAll(
        @RequestParam(required = true) dailyReportId: UUID,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<DailyActivityResponse>> {
        val pageable = PageableDTO(sort = sort, direction = direction, pageNumber = pageNumber, pageSize = pageSize)
        val result = searchDailyActivityUseCase.getAll(dailyReportId, pageable).getOrThrow()
        return ResponseEntity.ok(result.map { dailyActivityMapper.toResponse(it) })
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar atividade diária por ID público", description = "Retorna os detalhes de uma atividade diária específica.")
    fun getById(
        @Parameter(description = "ID público da atividade diária") @PathVariable id: UUID,
    ): ResponseEntity<DailyActivityResponse> {
        val result = searchDailyActivityUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(dailyActivityMapper.toResponse(result))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma atividade diária", description = "Deleta uma atividade diária a partir do seu ID público.")
    fun delete(
        @Parameter(description = "ID público da atividade diária") @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        deleteDailyActivityUseCase.execute(id).getOrThrow()
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    @Operation(summary = "Adiciona uma nova atividade a um relatório diário")
    fun add(
        @RequestBody @Valid form: DailyActivityRequest,
    ): ResponseEntity<Unit> {
        val activity = addActivityToDailyReportUseCase.execute(form).getOrThrow()
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(activity.publicId)
                .toUri()
        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma atividade diária existente")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid form: DailyActivityRequest,
    ): ResponseEntity<Unit> {
        updateDailyActivityUseCase.execute(id, form).getOrThrow()
        return ResponseEntity.ok().build()
    }
}
