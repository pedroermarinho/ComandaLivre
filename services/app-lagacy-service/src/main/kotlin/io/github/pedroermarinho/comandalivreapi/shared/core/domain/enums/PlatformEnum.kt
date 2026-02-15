package io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums

enum class PlatformEnum(
    val value: String,
) {
    PWA("pwa"),
    ANDROID("android"),
    IOS("ios"),
    DESKTOP("desktop"),
    ;

    fun matches(value: String): Boolean = this.value.equals(value, ignoreCase = true)
}
