package io.github.pedroermarinho.user.domain.enums

enum class FeatureEnum(
    val value: String,
) {
    COMANDALIVRE_SELF_ORDER("comandalivre_self_order"),
    ADMIN_DASHBOARD_ACCESS("admin_dashboard_access"),
    COMMAND_CREATION_ACCESS("command_creation_access"),
    PRODUCT_MODIFIERS_MANAGEMENT("product_modifiers_management"),
    TABLE_MANAGEMENT_ACCESS("table_management_access"),
    USER_ROLE_MANAGEMENT("user_role_management"),
    ;

    fun matches(value: String): Boolean = this.value.equals(value, ignoreCase = true)
}
