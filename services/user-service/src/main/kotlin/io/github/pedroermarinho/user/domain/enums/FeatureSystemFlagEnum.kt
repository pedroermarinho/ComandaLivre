package io.github.pedroermarinho.user.domain.enums

import io.github.pedroermarinho.shared.exceptions.NotFoundException

enum class FeatureSystemFlagEnum(
    val keyFlag: String,
) {
    AI_PRODUCT_DESCRIPTION("ai_product_description"),
    S3_INTEGRATION("s3_integration"),
    EMAIL_SENDING("email_sending"),
    WHATSAPP_MESSAGING("whatsapp_messaging"),
    PERSONALIZED_RECOMMENDATIONS("personalized_recommendations"),
    PROXIMITY_SORTING("proximity_sorting"),
    DISCORD_WEBHOOK("discord_webhook"),
    ;

    companion object {
        fun fromKey(key: String): FeatureSystemFlagEnum = entries.find { it.keyFlag == key } ?: throw NotFoundException("feature flag not found")
    }
}
