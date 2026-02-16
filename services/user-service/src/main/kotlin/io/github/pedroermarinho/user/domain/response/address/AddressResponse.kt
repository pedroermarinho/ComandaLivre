package io.github.pedroermarinho.user.domain.response.address

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação do endereço de um restaurante.")
data class AddressResponse(
    @param:Schema(description = "UUID público do endereço.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val id: UUID,
    @param:Schema(description = "Rua do endereço.", example = "Avenida Paulista")
    val street: String,
    @param:Schema(description = "Número do endereço.", example = "1000")
    val number: String,
    @param:Schema(description = "CEP do endereço.", example = "01310-000")
    val zipCode: String,
    @param:Schema(description = "Cidade do endereço.", example = "São Paulo")
    val city: String,
    @param:Schema(description = "Estado do endereço.", example = "SP")
    val state: String,
    val neighborhood: String,
    val complement: String? = null,
    @param:Schema(description = "Data de criação do endereço.", example = "2023-10-15T10:15:30")
    val createdAt: LocalDateTime,
)
