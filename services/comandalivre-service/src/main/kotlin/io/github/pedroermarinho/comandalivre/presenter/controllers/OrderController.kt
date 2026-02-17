package io.github.pedroermarinho.comandalivre.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderFilterDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderForPrintingDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.CancelForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.order.ChangeStatusOrderRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.order.OrderCreateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.order.OrderResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.AddOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.CancelOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.ChangeStatusOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.GetOrderForPrintingUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.PrioritizeOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.RemoveOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.SearchOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.OrderMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/comandalivre/orders")
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos no sistema.")
class OrderController(
    private val searchOrderUseCase: SearchOrderUseCase,
    private val addOrderUseCase: AddOrderUseCase,
    private val removeOrderUseCase: RemoveOrderUseCase,
    private val changeStatusOrderUseCase: ChangeStatusOrderUseCase,
    private val prioritizeOrderUseCase: PrioritizeOrderUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val getOrderForPrintingUseCase: GetOrderForPrintingUseCase,
    private val orderMapper: OrderMapper,
) {
    @Operation(
        summary = "Buscar pedidos",
        description = "Retorna uma lista paginada de pedidos, com opções de filtro por comanda, empresa e status.",
    )
    @GetMapping
    fun getAll(
        @Parameter(description = "Número da página (inicia em 0).", example = "0")
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @Parameter(description = "Tamanho da página.", example = "10")
        @RequestParam(defaultValue = "10") pageSize: Int,
        @Parameter(description = "Campos para ordenação (ex: created_at, product.name).")
        @RequestParam(required = false) sort: List<String>?,
        @Parameter(description = "Direção da ordenação (asc ou desc).")
        @RequestParam(required = false) direction: String?,
        @Parameter(description = "ID público da comanda para filtrar pedidos.")
        @RequestParam(required = false) commandId: UUID?,
        @Parameter(description = "ID público da empresa para filtrar pedidos.")
        @RequestParam(required = false) companyId: UUID?,
        @Parameter(description = "Status do pedido para filtrar (ex: PENDING, DELIVERED).")
        @RequestParam(required = false) status: List<String>?,
    ): ResponseEntity<PageDTO<OrderResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val filter =
            OrderFilterDTO(
                commandPublicId = commandId,
                companyPublicId = companyId,
                status = status,
            )
        val result = searchOrderUseCase.getAll(pageable, filter).getOrThrow()
        return ResponseEntity.ok(result.map { orderMapper.toResponse(it) })
    }

    @Operation(
        summary = "Buscar pedido por ID",
        description = "Retorna um pedido específico pelo seu ID público.",
    )
    @GetMapping("/{id}")
    fun getById(
        @Parameter(description = "ID público do pedido.", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<OrderResponse> {
        val result = searchOrderUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(orderMapper.toResponse(result))
    }

    @Operation(
        summary = "Criar novo pedido",
        description = "Cria um novo pedido com um ou mais itens.",
    )
    @PostMapping
    @Transactional
    fun add(
        @Parameter(description = "Dados para criação do pedido.", required = true)
        @Valid
        @RequestBody orderCreateRequest: OrderCreateRequest,
    ): ResponseEntity<Unit> {
        addOrderUseCase.add(orderCreateRequest).getOrThrow()
        val location: URI =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/?commandId={commandId}")
                .buildAndExpand(orderCreateRequest.commandId)
                .toUri()
        return ResponseEntity.created(location).build()
    }

    @Operation(
        summary = "Remover pedido",
        description = "Remove um pedido existente pelo seu ID.",
    )
    @DeleteMapping("/{id}")
    @Transactional
    fun remove(
        @Parameter(description = "ID público do pedido a ser removido.", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        removeOrderUseCase.execute(id).getOrThrow()
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Alterar status do pedido",
        description = "Altera o status de um pedido existente.",
    )
    @PatchMapping("/{id}/status")
    @Transactional
    fun changeStatus(
        @Parameter(description = "ID público do pedido.", required = true)
        @PathVariable id: UUID,
        @Parameter(description = "Novo status para o pedido.", required = true)
        @Valid
        @RequestBody form: ChangeStatusOrderRequest,
    ): ResponseEntity<Unit> {
        changeStatusOrderUseCase.execute(id, form.status).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Priorizar pedido",
        description = "Marca um pedido como prioritário para a cozinha/bar.",
    )
    @PatchMapping("/{id}/prioritize")
    @Transactional
    fun prioritize(
        @Parameter(description = "ID público do pedido a ser priorizado.", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        // TODO: implementação mock
//        prioritizeOrderUseCase.execute(publicId)
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Cancelar pedido",
        description = "Cancela um pedido existente, registrando o motivo.",
    )
    @PostMapping("/{id}/cancel")
    @Transactional
    fun cancel(
        @Parameter(description = "ID público do pedido a ser cancelado.", required = true)
        @PathVariable id: UUID,
        @Parameter(description = "Formulário com o motivo do cancelamento.", required = true)
        @Valid
        @RequestBody form: CancelForm,
    ): ResponseEntity<Unit> {
        cancelOrderUseCase.execute(id, form).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Obter dados para impressão de pedido",
        description = "Retorna os dados formatados de um pedido para impressão em via de produção (cozinha/bar).",
    )
    @GetMapping("/{id}/print-data")
    fun getPrintData(
        @Parameter(description = "ID público do pedido.", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<OrderForPrintingDTO> {
        // TODO: implementação mock
        val result = getOrderForPrintingUseCase.execute(id)
        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "Verificar status de fechamento da comanda",
        description = "Verifica se todos os pedidos de uma comanda estão em status fechado.",
    )
    @GetMapping("/is-command-fully-closed")
    fun isCommandFullyClosed(
        @Parameter(description = "ID público da comanda.", required = true)
        @RequestParam commandId: UUID,
    ): ResponseEntity<Boolean> {
        val result = searchOrderUseCase.isCommandFullyClosed(commandId).getOrThrow()
        return ResponseEntity.ok(result)
    }
}
