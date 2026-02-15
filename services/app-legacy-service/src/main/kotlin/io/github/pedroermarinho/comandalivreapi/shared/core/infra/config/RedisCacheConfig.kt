package io.github.pedroermarinho.comandalivreapi.shared.core.infra.config

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.CacheName
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

@Configuration
@EnableCaching
@ConditionalOnProperty(name = ["app.cache.type"], havingValue = "redis")
class RedisCacheConfig(
    private val redisConnectionFactory: RedisConnectionFactory,
) {
    @Bean
    fun cacheManager(): CacheManager {
        val cacheConfigs: Map<String, RedisCacheConfiguration> =
            CacheName.entries.associate { cache ->
                val config =
                    RedisCacheConfiguration
                        .defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(cache.ttlMinutes))
                        .disableCachingNullValues()
                cache.cacheName to config
            }

        return RedisCacheManager
            .builder(redisConnectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues())
            .withInitialCacheConfigurations(cacheConfigs)
            .transactionAware()
            .build()
    }
}
