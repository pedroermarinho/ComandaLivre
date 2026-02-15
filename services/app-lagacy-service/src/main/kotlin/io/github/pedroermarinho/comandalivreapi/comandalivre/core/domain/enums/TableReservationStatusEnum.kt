package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums

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
