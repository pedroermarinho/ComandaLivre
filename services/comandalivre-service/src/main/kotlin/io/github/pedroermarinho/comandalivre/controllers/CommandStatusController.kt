package io.github.pedroermarinho.comandalivre.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.command.CommandStatusResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.SearchCommandStatusUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.CommandStatusMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/comandalivre/command-status")
@Tag(name = "Status das Comandas", description = "Endpoints para gerenciamento dos status das comandas.")
class CommandStatusController(
    private val searchCommandStatusUseCase: SearchCommandStatusUseCase,
    private val commandStatusMapper: CommandStatusMapper,
) {
    @Operation(
        summary = "Buscar todos os status de comandas",
        description = "Retorna uma lista paginada de todos os status de comandas disponíveis.",
    )
    @GetMapping
    fun getAll(
        @Parameter(description = "Número da página (inicia em 0).", example = "0")
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @Parameter(description = "Tamanho da página.", example = "10")
        @RequestParam(defaultValue = "10") pageSize: Int,
        @Parameter(description = "Campos para ordenação (ex: name, key).")
        @RequestParam(required = false) sort: List<String>?,
        @Parameter(description = "Direção da ordenação (asc ou desc).")
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<CommandStatusResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchCommandStatusUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { commandStatusMapper.toResponse(it) })
    }
}
