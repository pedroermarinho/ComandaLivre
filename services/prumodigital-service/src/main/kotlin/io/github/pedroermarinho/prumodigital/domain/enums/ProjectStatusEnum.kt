package io.github.pedroermarinho.prumodigital.domain.enums

enum class ProjectStatusEnum(
    val value: String,
) {
    PLANNING("planning"),
    BIDDING("bidding"),
    PRE_CONSTRUCTION("pre_construction"),
    IN_PROGRESS("in_progress"),
    ON_HOLD("on_hold"),
    COMPLETED("completed"),
    POST_CONSTRUCTION("post_construction"),
    WARRANTY_PERIOD("warranty_period"),
    CANCELED("canceled"),
    ARCHIVED("archived"),
    ;

    fun matches(value: String): Boolean = this.value.equals(value, ignoreCase = true)
}
