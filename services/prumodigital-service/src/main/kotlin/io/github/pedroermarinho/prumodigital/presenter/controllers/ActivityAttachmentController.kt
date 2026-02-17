package io.github.pedroermarinho.prumodigital.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.ActivityAttachmentForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.activityattachment.AddAttachmentToActivityUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/prumodigital/activity-attachments")
@Tag(name = "Anexos de Atividade", description = "Endpoints para gerenciar anexos de atividades diárias.")
class ActivityAttachmentController(
    private val addAttachmentToActivityUseCase: AddAttachmentToActivityUseCase,
) {
    @PostMapping
    @Operation(
        summary = "Adicionar anexo a uma atividade",
        description = "Adiciona um novo anexo (imagem, documento, etc.) a uma atividade diária específica.",
    )
    fun add(
        @Parameter(description = "Dados do anexo a ser adicionado.", required = true)
        @RequestBody
        @Valid form: ActivityAttachmentForm,
    ): ResponseEntity<Unit> {
        addAttachmentToActivityUseCase.execute(form).getOrThrow()
        return ResponseEntity.ok().build()
    }
}
