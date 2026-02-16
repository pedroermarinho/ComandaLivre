package io.github.pedroermarinho.comandalivre.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.order.OrderStatusResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.SearchStatusOrderUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.OrderStatusMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comandalivre/order-status")
@Tag(name = "Status de Pedidos", description = "Endpoints para gerenciamento de status de pedidos.")
class StatusOrderController(
    private val searchStatusOrderUseCase: SearchStatusOrderUseCase,
    private val orderStatusMapper: OrderStatusMapper,
) {
    @Operation(
        summary = "Buscar todos os status de pedidos",
        description = "Retorna uma lista de todos os status de pedidos disponíveis.",
    )
    @GetMapping
    fun getAll(): ResponseEntity<List<OrderStatusResponse>> {
        val result = searchStatusOrderUseCase.getAll().getOrThrow()
        return ResponseEntity.ok(result.map { orderStatusMapper.toResponse(it) })
    }
}
