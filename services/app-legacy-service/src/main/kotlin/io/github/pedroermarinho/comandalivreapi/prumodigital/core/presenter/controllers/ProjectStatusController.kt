package io.github.pedroermarinho.comandalivreapi.prumodigital.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.ProjectStatusResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchProjectStatusUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.ProjectStatusMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/prumodigital/project-status")
@Tag(name = "Status do Projeto", description = "Endpoints para gerenciar os status dos projetos")
class ProjectStatusController(
    private val searchProjectStatusUseCase: SearchProjectStatusUseCase,
    private val projectStatusMapper: ProjectStatusMapper,
) {
    @GetMapping
    @Operation(summary = "Lista todos os status de projeto")
    fun getAll(): ResponseEntity<List<ProjectStatusResponse>> {
        val result = searchProjectStatusUseCase.getAll().getOrThrow()
        return ResponseEntity.ok(result.map { projectStatusMapper.toResponse(it) })
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um status de projeto por ID")
    fun getById(
        @PathVariable id: UUID,
    ): ResponseEntity<ProjectStatusResponse> {
        val projectStatus = searchProjectStatusUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(projectStatusMapper.toResponse(projectStatus))
    }

    @GetMapping("/key/{key}")
    @Operation(summary = "Busca um status de projeto por chave")
    fun getByKey(
        @PathVariable key: String,
    ): ResponseEntity<ProjectStatusResponse> {
        val projectStatus = searchProjectStatusUseCase.getByKey(key).getOrThrow()
        return ResponseEntity.ok(projectStatusMapper.toResponse(projectStatus))
    }
}
