package io.github.pedroermarinho.company.domain.enums

enum class EmployeeInviteEnum(
    val value: String,
) {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    EXPIRED("expired"),
}
