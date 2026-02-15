package io.github.pedroermarinho.comandalivreapi.shared.core.domain.event

import java.util.*

data class CompanyCreatedEvent(
    val companyPublicId: UUID,
    val companyName: String,
    val companyEmail: String?,
    val ownerId: Int,
    val ownerName: String,
    val ownerEmail: String,
    val ownerSub: String,
)
