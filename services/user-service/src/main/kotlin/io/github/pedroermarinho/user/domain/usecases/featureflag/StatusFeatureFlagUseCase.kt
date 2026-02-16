package io.github.pedroermarinho.user.domain.usecases.featureflag

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.enums.CacheConstants
import io.github.pedroermarinho.user.domain.enums.FeatureSystemFlagEnum
import io.github.pedroermarinho.user.domain.repositories.FeatureFlagRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class StatusFeatureFlagUseCase(
    private val featureFlagRepository: FeatureFlagRepository,
) {
    @Cacheable(value = [CacheConstants.FEATURE_SYSTEM_FLAG], key = "#featureFlag.keyFlag")
    fun isEnabled(featureFlag: FeatureSystemFlagEnum): Boolean = featureFlagRepository.isFeatureEnabled(featureFlag.keyFlag).getOrThrow()
}
