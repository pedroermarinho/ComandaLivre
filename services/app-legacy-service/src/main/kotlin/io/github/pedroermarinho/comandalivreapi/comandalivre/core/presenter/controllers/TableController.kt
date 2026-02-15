package io.github.pedroermarinho.comandalivreapi.comandalivre.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.table.TableUpdateForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table.TableBulkCreateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table.TableCreateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.table.TableResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.CreateTableUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.DeleteTableUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.SearchTableUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.UpdateTableUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.TableMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/v1/comandalivre/tables")
@Tag(name = "Mesas", description = "Endpoints para gerenciamento de mesas do sistema.")
class TableController(
    private val searchTableUseCase: SearchTableUseCase,
    private val createTableUseCase: CreateTableUseCase,
    private val updateTableUseCase: UpdateTableUseCase,
    private val deleteTableUseCase: DeleteTableUseCase,
    private val tableMapper: TableMapper,
) {
    @Operation(
        summary = "Buscar mesas",
        description = "Retorna uma lista paginada de mesas de um restaurante, com opções de ordenação.",
    )
    @GetMapping
    fun getAll(
        @Parameter(description = "Número da página (inicia em 0).", example = "0")
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @Parameter(description = "Tamanho da página.", example = "10")
        @RequestParam(defaultValue = "10") pageSize: Int,
        @Parameter(description = "Campos para ordenação (ex: name, num_people).")
        @RequestParam(required = false) sort: List<String>? = listOf("name"),
        @Parameter(description = "Direção da ordenação (asc ou desc).")
        @RequestParam(required = false) direction: String?,
        @Parameter(description = "ID público da empresa para filtrar as mesas.", required = true)
        @RequestParam(required = true) companyId: UUID,
    ): ResponseEntity<PageDTO<TableResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchTableUseCase.getAll(pageable, companyId).getOrThrow()
        return ResponseEntity.ok(result.map { tableMapper.toResponse(it) })
    }

    @Operation(
        summary = "Buscar todas as mesas (não paginada)",
        description = "Retorna uma lista completa de todas as mesas de um restaurante, sem paginação.",
    )
    @GetMapping("/list")
    fun getAllList(
        @Parameter(description = "ID público da empresa para buscar as mesas.", required = true)
        @RequestParam(required = true) companyId: UUID,
    ): ResponseEntity<List<TableResponse>> {
        val result = searchTableUseCase.getAllList(companyId).getOrThrow()
        return ResponseEntity.ok(result.map { tableMapper.toResponse(it) })
    }

    @Operation(
        summary = "Buscar mesa por ID",
        description = "Retorna os detalhes de uma mesa específica pelo seu ID público.",
    )
    @GetMapping("/{id}")
    fun getById(
        @Parameter(description = "ID público da mesa.", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<TableResponse> {
        val result = searchTableUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(tableMapper.toResponse(result))
    }

    @Operation(
        summary = "Criar mesa",
        description = "Cria uma nova mesa para um restaurante.",
    )
    @PostMapping
    @Transactional
    fun create(
        @Parameter(description = "Dados para criação da mesa.", required = true)
        @Valid
        @RequestBody tableCreateRequest: TableCreateRequest,
        uriBuilder: UriComponentsBuilder,
    ): ResponseEntity<Unit> {
        val result = createTableUseCase.create(tableCreateRequest).getOrThrow()
        val uri = uriBuilder.path("/api/v1/tables/{id}").buildAndExpand(result.publicId).toUri()
        return ResponseEntity.created(uri).build()
    }

    @Operation(
        summary = "Criar mesas em lote",
        description = "Cria uma sequência de mesas para um restaurante, ignorando as que já existem.",
    )
    @PostMapping("/bulk")
    @Transactional
    fun createBulk(
        @Parameter(description = "Dados para criação de mesas em lote.", required = true)
        @Valid
        @RequestBody form: TableBulkCreateRequest,
    ): ResponseEntity<Unit> {
        createTableUseCase.create(form).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Atualizar mesa",
        description = "Atualiza os dados de uma mesa existente.",
    )
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "ID público da mesa a ser atualizada.", required = true)
        @PathVariable id: UUID,
        @Parameter(description = "Dados para atualização da mesa.", required = true)
        @Valid
        @RequestBody tableUpdateForm: TableUpdateForm,
    ): ResponseEntity<Unit> {
        updateTableUseCase.update(id, tableUpdateForm).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Deletar mesa",
        description = "Remove uma mesa do restaurante.",
    )
    @DeleteMapping("/{id}")
    @Transactional
    fun delete(
        @Parameter(description = "ID público da mesa a ser deletada.", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        deleteTableUseCase.execute(id).getOrThrow()
        return ResponseEntity.noContent().build()
    }
}
