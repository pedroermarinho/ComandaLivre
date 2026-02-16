package io.github.pedroermarinho.user.domain.dtos.user

import java.time.LocalDate

data class UserRegistrationsPerDayDTO(
    val date: LocalDate,
    val userCount: Int,
)
