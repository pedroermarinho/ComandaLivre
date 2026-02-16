package io.github.pedroermarinho.user.infra.config

import com.github.benmanes.caffeine.cache.Caffeine
import io.github.pedroermarinho.user.domain.enums.CacheName
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
@ConditionalOnProperty(name = ["app.cache.type"], havingValue = "caffeine")
class CaffeineCacheConfig {
    @Bean
    fun cacheManager(): CacheManager {
        val caches =
            CacheName.entries.associate { cache ->
                cache.cacheName to
                    Caffeine
                        .newBuilder()
                        .recordStats()
                        .expireAfterWrite(cache.ttlMinutes, TimeUnit.MINUTES)
                        .maximumSize(cache.maxSize)
                        .build<Any, Any>()
            }
        val manager = SimpleCacheManager()
        manager.setCaches(caches.map { (name, caffeine) -> CaffeineCache(name, caffeine) })
        return manager
    }
}
