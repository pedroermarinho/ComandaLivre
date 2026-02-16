package io.github.pedroermarinho.prumodigital.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.project.ProjectCreateRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.ProjectResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.CreateProjectUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchProjectUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.UpdateProjectUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.ProjectMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/api/v1/prumodigital/projects")
@Tag(name = "Projetos", description = "Endpoints para gerenciar projetos")
class ProjectController(
    private val createProjectUseCase: CreateProjectUseCase,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val projectMapper: ProjectMapper,
) {
    @Operation(summary = "Buscar todos os projetos", description = "Buscar todos os projetos")
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
        @RequestParam(required = false) search: String?,
    ): ResponseEntity<PageDTO<ProjectResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
                search = search,
            )

        val result = searchProjectUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { projectMapper.toResponse(it) })
    }

    @PostMapping
    @Operation(summary = "Cria um novo projeto")
    fun create(
        @RequestBody @Valid form: ProjectCreateRequest,
    ): ResponseEntity<Unit> {
        val projectId = createProjectUseCase.execute(form).getOrThrow()
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(projectId.publicId)
                .toUri()
        return ResponseEntity.created(location).build()
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um projeto por ID")
    fun getById(
        @PathVariable id: UUID,
    ): ResponseEntity<ProjectResponse> {
        val result = searchProjectUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(projectMapper.toResponse(result))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um projeto existente")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid form: ProjectCreateRequest,
    ): ResponseEntity<Unit> {
        updateProjectUseCase.execute(id, form).getOrThrow()
        return ResponseEntity.ok().build()
    }
}
