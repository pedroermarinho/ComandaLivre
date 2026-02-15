package io.github.pedroermarinho.comandalivreapi.comandalivre.core.presenter.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comandalivre/dashboard")
@Tag(name = "Dashboard", description = "Endpoints para dados e métricas do dashboard.")
class DashboardController {
    @Schema(description = "Representa o estado inicial do dashboard.")
    data class DashboardStateDTO(
        @param:Schema(description = "Mensagem de status ou informação inicial.", example = "Estado inicial do dashboard carregado com sucesso.")
        val message: String,
    )

    @Operation(
        summary = "Obter estado inicial do dashboard",
        description = "Retorna o estado inicial e dados básicos para o dashboard.",
    )
    @GetMapping("/initial-state")
    fun getInitialState(): ResponseEntity<DashboardStateDTO> {
        // Lógica para buscar o estado inicial será implementada no UseCase
        val initialState = DashboardStateDTO(message = "Estado inicial do dashboard")
        return ResponseEntity.ok(initialState)
    }
}
