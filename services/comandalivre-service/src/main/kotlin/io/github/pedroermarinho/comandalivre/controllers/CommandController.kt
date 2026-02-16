package io.github.pedroermarinho.comandalivre.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.BillDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandFilterDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.command.ChangeStatusCommandRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.command.ChangeTableCommandRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.command.CommandRequestForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.command.CommandResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.ChangeCommandTableUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.ChangeStatusCommandUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.CreateCommandUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.GetBillForPrintingUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.SearchCommandUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.CommandMapper
import io.github.pedroermarinho.user.domain.annotations.RequirePermissions
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.enums.FeatureEnum
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
@RequestMapping("/api/v1/comandalivre/commands")
@Tag(name = "Comandas", description = "Endpoints para gerenciamento de comandas no sistema.")
class CommandController(
    private val searchCommandUseCase: SearchCommandUseCase,
    private val createCommandUseCase: CreateCommandUseCase,
    private val changeStatusCommandUseCase: ChangeStatusCommandUseCase,
    private val changeCommandTableUseCase: ChangeCommandTableUseCase,
    private val getBillForPrintingUseCase: GetBillForPrintingUseCase,
    private val commandMapper: CommandMapper,
) {
    @Operation(
        summary = "Buscar comandas",
        description = "Retorna uma lista paginada de comandas, com opções de filtro por mesa, empresa e status.",
    )
    @GetMapping
    fun getAll(
        @Parameter(description = "Número da página (inicia em 0).", example = "0")
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @Parameter(description = "Tamanho da página.", example = "10")
        @RequestParam(defaultValue = "10") pageSize: Int,
        @Parameter(description = "Campos para ordenação (ex: created_at, name).")
        @RequestParam(required = false) sort: List<String>? = listOf("created_at"),
        @Parameter(description = "Direção da ordenação (asc ou desc).")
        @RequestParam(required = false) direction: String? = "desc",
        @Parameter(description = "ID público da mesa para filtrar comandas.")
        @RequestParam(required = false) tableId: UUID? = null,
        @Parameter(description = "ID público da empresa para filtrar comandas.")
        @RequestParam(required = false) companyId: UUID? = null,
        @Parameter(description = "Status da comanda para filtrar (ex: OPEN, CLOSED).")
        @RequestParam(required = false) status: List<CommandStatusEnum>? = null,
    ): ResponseEntity<PageDTO<CommandResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val filter =
            CommandFilterDTO(
                tableId = tableId,
                status = status,
                companyId = companyId,
            )
        val result = searchCommandUseCase.getAll(pageable, filter).getOrThrow()
        return ResponseEntity.ok(result.map { commandMapper.toResponse(it) })
    }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Contar comandas processadas",
        description = "Retorna a quantidade total de comandas processadas no sistema.",
    )
    @GetMapping("/count")
    fun count(): ResponseEntity<Long> {
        val result = searchCommandUseCase.count().getOrThrow()
        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "Buscar comanda por ID",
        description = "Retorna os detalhes de uma comanda específica pelo seu ID público.",
    )
    @GetMapping("/{commandId}")
    fun getById(
        @Parameter(description = "ID público da comanda.", required = true)
        @PathVariable commandId: UUID,
    ): ResponseEntity<CommandResponse> {
        val result = searchCommandUseCase.getById(commandId).getOrThrow()
        return ResponseEntity.ok(commandMapper.toResponse(result))
    }

    @Operation(
        summary = "Criar nova comanda",
        description = "Cria uma nova comanda para uma mesa específica.",
    )
    @PostMapping
    @Transactional
    fun create(
        @Parameter(description = "Dados para criação da comanda.", required = true)
        @Valid
        @RequestBody commandRequestForm: CommandRequestForm,
        uriBuilder: UriComponentsBuilder,
    ): ResponseEntity<Unit> {
        val result = createCommandUseCase.create(commandRequestForm).getOrThrow()
        val uri = uriBuilder.path("/api/v1/commands/{commandId}").buildAndExpand(result.publicId).toUri()
        return ResponseEntity.created(uri).build()
    }

    @Operation(
        summary = "Alterar status da comanda",
        description = "Altera o status de uma comanda existente.",
    )
    @PatchMapping("/{commandId}/status")
    fun changeStatus(
        @Parameter(description = "ID público da comanda.", required = true)
        @PathVariable commandId: UUID,
        @Parameter(description = "Novo status e opção de fechar todas as ordens.", required = true)
        @Valid
        @RequestBody form: ChangeStatusCommandRequest,
    ): ResponseEntity<Unit> {
        changeStatusCommandUseCase.execute(commandId, form.status, form.closeAll).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Trocar mesa da comanda",
        description = "Move uma comanda de uma mesa para outra.",
    )
    @PatchMapping("/{commandId}/change-table")
    fun changeTable(
        @Parameter(description = "ID público da comanda.", required = true)
        @PathVariable commandId: UUID,
        @Parameter(description = "ID da nova mesa para a comanda.", required = true)
        @Valid
        @RequestBody changeTableCommandRequest: ChangeTableCommandRequest,
    ): ResponseEntity<Unit> {
        changeCommandTableUseCase.execute(commandId, changeTableCommandRequest.newTableId).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Obter dados da conta para impressão",
        description = "Retorna os dados formatados da conta de uma comanda para impressão.",
    )
    @GetMapping("/{commandId}/bill-data")
    fun getBillData(
        @Parameter(description = "ID público da comanda.", required = true)
        @PathVariable commandId: UUID,
    ): ResponseEntity<BillDTO> {
        val result = getBillForPrintingUseCase.execute(commandId).getOrThrow()
        return ResponseEntity.ok(result)
    }
}
