package io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums

enum class GroupEnum(
    val value: String,
) {
    DEFAULT_USER_CL("default_user_cl"),
    DEFAULT_USER_PD("default_user_pd"),
    ADMIN_SYSTEM("admin_system"),
    ;

    fun matches(value: String): Boolean = this.value.equals(value, ignoreCase = true)
}
