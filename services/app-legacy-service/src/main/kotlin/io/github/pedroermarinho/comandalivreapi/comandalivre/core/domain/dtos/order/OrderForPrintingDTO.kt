package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order

import java.util.UUID

data class OrderForPrintingDTO(
    val publicId: UUID,
    val productName: String,
    val quantity: Int,
    val notes: String?,
)
