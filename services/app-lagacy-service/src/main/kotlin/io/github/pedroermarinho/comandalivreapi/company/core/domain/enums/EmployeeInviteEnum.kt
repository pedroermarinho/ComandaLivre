package io.github.pedroermarinho.comandalivreapi.company.core.domain.enums

enum class EmployeeInviteEnum(
    val value: String,
) {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    EXPIRED("expired"),
}
