package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductResponse
import java.math.BigDecimal

data class BillItemDTO(
    val product: ProductResponse,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal,
)
