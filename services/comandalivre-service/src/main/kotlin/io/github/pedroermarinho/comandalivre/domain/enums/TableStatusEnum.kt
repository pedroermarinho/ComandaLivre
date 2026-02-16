package io.github.pedroermarinho.comandalivre.domain.enums

enum class TableStatusEnum(
    val value: String,
) {
    AVAILABLE("available"),
    OCCUPIED("occupied"),
    RESERVED("reserved"),
    CLEANING("cleaning"),
    UNAVAILABLE("unavailable"),
    AWAITING_PAYMENT("awaiting_payment"),
}
