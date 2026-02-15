package io.github.pedroermarinho.comandalivreapi.comandalivre.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.table.TableStatusResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.SearchTableStatusUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.TableStatusMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comandalivre/table-status")
@Tag(name = "Status de Mesas", description = "Endpoints para gerenciamento de status de mesas.")
class TableStatusController(
    private val searchTableStatusUseCase: SearchTableStatusUseCase,
    private val tableStatusMapper: TableStatusMapper,
) {
    @Operation(
        summary = "Buscar status de mesas",
        description = "Retorna uma lista paginada de todos os status de mesas disponíveis.",
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
    ): ResponseEntity<PageDTO<TableStatusResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )

        val result = searchTableStatusUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { tableStatusMapper.toResponse(it) })
    }
}
