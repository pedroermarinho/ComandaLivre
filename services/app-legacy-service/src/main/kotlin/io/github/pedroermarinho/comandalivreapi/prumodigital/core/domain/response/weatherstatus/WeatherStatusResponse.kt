package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.weatherstatus

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação de um status climático.")
data class WeatherStatusResponse(
    @param:Schema(description = "ID público do status climático.")
    val id: UUID,
    @param:Schema(description = "Chave textual única do status (ex: SUNNY, CLOUDY, RAINY).")
    val key: String,
    @param:Schema(description = "Nome legível do status (ex: Ensolarado, Nublado, Chuvoso).")
    val name: String,
    @param:Schema(description = "Descrição detalhada do status.")
    val description: String?,
    @param:Schema(description = "Ícone associado ao status climático.")
    val icon: String?,
    @param:Schema(description = "Data e hora de criação do status.")
    val createdAt: LocalDateTime,
)
