package io.github.pedroermarinho.comandalivre.domain.forms.table

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Representação de uma mesa em um restaurante.")
data class TableUpdateForm(
    @param:NotBlank
    @param:Schema(description = "Nome ou identificador da mesa.", example = "Mesa 1")
    val name: String,
    @param:Schema(description = "Número de pessoas que a mesa suporta.", example = "4")
    val numPeople: Int,
    @param:Schema(description = "Descrição adicional da mesa.", example = "Perto da janela")
    val description: String?,
)
