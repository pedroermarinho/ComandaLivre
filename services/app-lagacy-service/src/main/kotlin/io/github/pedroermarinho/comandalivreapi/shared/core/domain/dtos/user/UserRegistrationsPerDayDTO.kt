package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user

import java.time.LocalDate

data class UserRegistrationsPerDayDTO(
    val date: LocalDate,
    val userCount: Int,
)
