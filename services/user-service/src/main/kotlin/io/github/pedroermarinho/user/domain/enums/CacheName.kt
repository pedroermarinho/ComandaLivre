package io.github.pedroermarinho.user.domain.enums

enum class CacheName(
    val cacheName: String,
    val ttlMinutes: Long,
    val maxSize: Long,
) {
    FEATURE_SYSTEM_FLAG(CacheConstants.FEATURE_SYSTEM_FLAG, 10, 20),
    USER(CacheConstants.USER, 5, 1000),
    FEATURE_USER_PERMISSION(CacheConstants.FEATURE_USER_PERMISSION, 5, 1000),
}

object CacheConstants {
    const val USER = "user"
    const val FEATURE_SYSTEM_FLAG = "feature_system_flag"
    const val FEATURE_USER_PERMISSION = "feature_user_permission"
}
