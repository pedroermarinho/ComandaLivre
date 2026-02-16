package io.github.pedroermarinho.user.domain.dtos.address

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class AddressDTO(
    val id: EntityId,
    val street: String,
    val number: String,
    val zipCode: String,
    val city: String,
    val state: String,
    val neighborhood: String,
    val complement: String? = null,
    val createdAt: LocalDateTime,
    val updateAt: LocalDateTime,
)
