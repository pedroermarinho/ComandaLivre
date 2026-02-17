package io.github.pedroermarinho.comandalivre.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.cashregister.CloseSessionRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.cashregister.StartSessionRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.cashregister.ClosingResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.cashregister.SessionResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.cashregister.CloseSessionUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.cashregister.SearchClosingUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.cashregister.SearchSessionUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.cashregister.StartSessionUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ClosingMapper
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.SessionMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/comandalivre/sessions")
@Tag(name = "Sessões de Caixa", description = "Endpoints para gerenciamento de sessões de caixa.")
class SessionController(
    private val startSessionUseCase: StartSessionUseCase,
    private val closeSessionUseCase: CloseSessionUseCase,
    private val searchSessionUseCase: SearchSessionUseCase,
    private val searchClosingUseCase: SearchClosingUseCase,
    private val sessionMapper: SessionMapper,
    private val closingMapper: ClosingMapper,
) {
    @PostMapping("/start")
    @Operation(
        summary = "Inicia uma nova sessão de caixa",
        description = "Registra o início de uma nova sessão de caixa para a empresa.",
    )
    fun startSession(
        @Parameter(description = "Dados para iniciar a sessão de caixa.", required = true)
        @Valid
        @RequestBody request: StartSessionRequest,
    ): ResponseEntity<Unit> {
        startSessionUseCase.execute(request).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @PostMapping("/close")
    @Operation(
        summary = "Fecha a sessão de caixa ativa",
        description = "Registra o fechamento da sessão de caixa ativa para a empresa, com os valores contados.",
    )
    fun closeSession(
        @Parameter(description = "Dados para fechar a sessão de caixa.", required = true)
        @Valid
        @RequestBody request: CloseSessionRequest,
    ): ResponseEntity<Unit> {
        closeSessionUseCase.execute(request).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @GetMapping("/active")
    @Operation(
        summary = "Obtém a sessão de caixa ativa",
        description = "Retorna os detalhes da sessão de caixa atualmente ativa para a empresa, se houver.",
    )
    fun getActiveSession(
        @Parameter(description = "ID público da empresa para buscar a sessão de caixa ativa.", required = true)
        @RequestParam companyId: UUID,
    ): ResponseEntity<SessionResponse> {
        val result = searchSessionUseCase.getActiveByCompanyId(companyId).getOrThrow()
        return ResponseEntity.ok(sessionMapper.toResponse(result))
    }

    @GetMapping("/{sessionId}/closing")
    @Operation(
        summary = "Obtém o fechamento de uma sessão",
        description = "Retorna os detalhes do fechamento de uma sessão de caixa específica pelo seu ID.",
    )
    fun getClosing(
        @Parameter(description = "ID público da sessão para a qual buscar o fechamento.", required = true)
        @PathVariable sessionId: UUID,
    ): ResponseEntity<ClosingResponse> {
        val result = searchClosingUseCase.getBySessionId(sessionId).getOrThrow()
        return ResponseEntity.ok(closingMapper.toResponse(result))
    }

    @GetMapping("/{sessionId}")
    @Operation(
        summary = "Obtém os detalhes de uma sessão de caixa",
        description = "Retorna os detalhes de uma sessão de caixa específica pelo seu ID.",
    )
    fun getById(
        @Parameter(description = "ID público da sessão para a qual buscar os detalhes.", required = true)
        @PathVariable sessionId: UUID,
    ): ResponseEntity<SessionResponse> {
        val result = searchSessionUseCase.getById(sessionId).getOrThrow()
        return ResponseEntity.ok(sessionMapper.toResponse(result))
    }
}
