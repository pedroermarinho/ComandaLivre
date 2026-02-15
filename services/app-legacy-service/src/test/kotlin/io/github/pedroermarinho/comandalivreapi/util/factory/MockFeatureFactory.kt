
package io.github.pedroermarinho.comandalivreapi.util.factory

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.feature.FeatureDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.FeatureEntity
import java.time.LocalDateTime

object MockFeatureFactory {
    fun buildFeatureEntity(): FeatureEntity =
        FeatureEntity.createNew(
            featureKey = "test.feature",
            name = "Test Feature",
            description = "A test feature",
        )

    fun buildFeatureDTO(): FeatureDTO {
        val entity = buildFeatureEntity()
        return FeatureDTO(
            id = entity.id,
            featureKey = entity.featureKey,
            name = entity.name,
            description = entity.description,
            createdAt = LocalDateTime.now(),
        )
    }
}
