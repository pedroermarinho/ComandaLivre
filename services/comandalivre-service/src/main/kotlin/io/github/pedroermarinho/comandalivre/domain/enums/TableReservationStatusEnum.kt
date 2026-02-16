package io.github.pedroermarinho.comandalivre.domain.enums

enum class TableReservationStatusEnum(
    val value: String,
) {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED"),
    ;

    companion object {
        fun fromValue(value: String): TableReservationStatusEnum? = entries.find { it.value == value }
    }
}
