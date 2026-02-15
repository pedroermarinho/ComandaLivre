
package io.github.pedroermarinho.comandalivreapi.util.factory

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.group.GroupDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.GroupEntity
import java.time.LocalDateTime

object MockGroupFactory {
    fun buildGroupEntity(): GroupEntity =
        GroupEntity.createNew(
            name = "Test Group",
            description = "A test group",
            groupKey = "test.group",
            createdAt = LocalDateTime.now(),
        )

    fun buildGroupDTO(): GroupDTO {
        val entity = buildGroupEntity()
        return GroupDTO(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            groupKey = entity.groupKey,
            createdAt = LocalDateTime.now(),
        )
    }
}
