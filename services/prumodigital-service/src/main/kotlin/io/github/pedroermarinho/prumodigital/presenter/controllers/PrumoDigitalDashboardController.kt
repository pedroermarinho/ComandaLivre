package io.github.pedroermarinho.prumodigital.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectCountByStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchProjectUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/prumodigital/dashboard")
@Tag(name = "Dashboard PrumoDigital", description = "Endpoints para dados agregados do dashboard PrumoDigital")
class PrumoDigitalDashboardController(
    private val searchProjectUseCase: SearchProjectUseCase,
) {
    @GetMapping("/projects-by-status")
    @Operation(summary = "Retorna a contagem de projetos agrupados por status")
    fun getProjectCountByStatus(): ResponseEntity<List<ProjectCountByStatusDTO>> {
        val result = searchProjectUseCase.getProjectCountByStatus().getOrThrow()
        return ResponseEntity.ok(result)
    }
}
