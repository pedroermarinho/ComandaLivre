package io.github.pedroermarinho.prumodigital.domain.response.activityattachment

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyactivity.DailyActivityResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação de um anexo de atividade diária.")
data class ActivityAttachmentResponse(
    @param:Schema(description = "ID público do anexo.")
    val id: UUID,
    @param:Schema(description = "ID público da atividade diária à qual o anexo pertence.")
    val dailyActivity: DailyActivityResponse,
    @param:Schema(description = "ID público do asset (arquivo) anexado.")
    val assetId: UUID,
    @param:Schema(description = "Descrição do anexo.")
    val description: String?,
    @param:Schema(description = "Data e hora de criação do anexo.")
    val createdAt: LocalDateTime,
)
