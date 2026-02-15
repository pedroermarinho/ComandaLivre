package io.github.pedroermarinho.comandalivreapi.prumodigital.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.project.EmployeeProjectAssignmentRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.EmployeeProjectAssignmentResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.AssignEmployeeToProjectUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchProjectTeamUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.EmployeeProjectAssignmentMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/prumodigital/projects/{projectId}/team")
@Tag(name = "Gerenciamento de equipes do projeto", description = "Endpoints para gerenciar equipes de projeto")
class ProjectTeamController(
    private val assignEmployeeToProjectUseCase: AssignEmployeeToProjectUseCase,
    private val searchProjectTeamUseCase: SearchProjectTeamUseCase,
    private val employeeProjectAssignmentMapper: EmployeeProjectAssignmentMapper,
) {
    @PostMapping
    @Operation(summary = "Designe um funcionário a um projeto")
    fun assignEmployee(
        @PathVariable projectId: UUID,
        @RequestBody @Valid form: EmployeeProjectAssignmentRequest,
    ): ResponseEntity<Unit> {
        assignEmployeeToProjectUseCase.execute(projectId, form).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @GetMapping
    @Operation(summary = "Listar os funcionários designados para um projeto")
    fun getAll(
        @PathVariable projectId: UUID,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
        @RequestParam(required = false) search: String?,
    ): ResponseEntity<PageDTO<EmployeeProjectAssignmentResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
                search = search,
            )
        val result = searchProjectTeamUseCase.getAll(pageable, projectId).getOrThrow()
        return ResponseEntity.ok(result.map { employeeProjectAssignmentMapper.toResponse(it) })
    }
}
