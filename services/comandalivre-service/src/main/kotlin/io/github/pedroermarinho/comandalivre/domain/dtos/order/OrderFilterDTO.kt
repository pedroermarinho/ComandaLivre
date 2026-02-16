package io.github.pedroermarinho.comandalivre.domain.dtos.order

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.OrderStatusEnum
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import java.util.*

data class OrderFilterDTO(
    val commandPublicId: UUID? = null,
    val companyPublicId: UUID? = null,
    val companyId: Int? = null,
    val status: List<String>? = null,
) {
    private val log = KotlinLogging.logger {}

    fun valid(): Result<Unit> {
        if (commandPublicId == null && (companyPublicId == null && companyId == null)) {
            log.error { "Nenhum filtro informado: commandId ou companyPublicId" }
            return Result.failure(
                BusinessLogicException(
                    "Pelo menos um dos filtros deve ser informado: command ou company",
                ),
            )
        }

        status?.forEach { statusKey ->
            OrderStatusEnum.fromValue(statusKey).getOrElse {
                log.error { "Status inválido: $statusKey" }
                return Result.failure(it)
            }
        }

        if (companyPublicId != null && companyId == null) {
            log.error { "Identificador da empresa não encontrado para o companyPublicId: $companyPublicId" }
            return Result.failure(
                BusinessLogicException(
                    "Identificador da empresa não encontrado",
                ),
            )
        }

        return Result.success(Unit)
    }
}
