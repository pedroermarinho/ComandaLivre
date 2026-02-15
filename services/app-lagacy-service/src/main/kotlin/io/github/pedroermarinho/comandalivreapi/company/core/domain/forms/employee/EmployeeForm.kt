package io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.employee

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Representação de um funcionário.")
data class EmployeeForm(
    @Schema(description = "UUID público do funcionário.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val publicId: UUID? = null,
    @Schema(description = "ID do tipo de função.", example = "1")
    val roleId: Int,
    @Schema(description = "ID do perfil de usuário associado.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val userId: Int,
    @Schema(description = "ID do restaurante associado.", example = "1")
    val companyId: Int,
)
