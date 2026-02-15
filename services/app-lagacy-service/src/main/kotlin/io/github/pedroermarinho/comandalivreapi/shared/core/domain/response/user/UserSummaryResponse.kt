package io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.user

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Representação do perfil de um usuário.")
data class UserSummaryResponse(
    @param:Schema(description = "UUID público do usuário.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val id: UUID,
    @param:Schema(description = "Nome do usuário.", example = "João da Silva")
    val name: String,
    @param:Schema(description = "URL da foto de perfil do usuário.", example = "https://example.com/images/profile.jpg")
    val avatarAssetId: Int?,
    @param:Schema(description = "Lista de chaves de recursos disponíveis para o usuário.")
    val featureKeys: List<String>,
)
