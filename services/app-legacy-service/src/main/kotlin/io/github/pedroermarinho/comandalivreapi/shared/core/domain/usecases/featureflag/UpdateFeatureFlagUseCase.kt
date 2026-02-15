package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.featureflag

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.CacheConstants
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.FeatureFlagRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateFeatureFlagUseCase(
    private val featureFlagRepository: FeatureFlagRepository,
    private val searchFeatureFlagUseCase: SearchFeatureFlagUseCase,
) {
    @CacheEvict(CacheConstants.FEATURE_SYSTEM_FLAG, allEntries = true)
    fun changeEnabled(
        publicId: UUID,
        enabled: Boolean,
    ): Result<Unit> =
        runCatching {
            val featureFlag = searchFeatureFlagUseCase.getEntityById(publicId).getOrThrow()
            featureFlagRepository.save(featureFlag.updateEnabled(enabled)).getOrThrow()
        }
}
