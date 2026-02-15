package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.notification

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.notification.NotificationDTO
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.NotificationEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.NotificationRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.NotificationMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.toDTO
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional(readOnly = true)
@UseCase
class SearchNotificationUseCase(
    private val notificationRepository: NotificationRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val notificationMapper: NotificationMapper,
) {
    fun getAll(pageable: PageableDTO): Result<PageDTO<NotificationDTO>> {
        val user = currentUserUseCase.getUser().getOrThrow()
        return notificationRepository
            .getByUserId(user.id.internalId, pageable)
            .map { page -> page.map { notificationMapper.toDTO(it, user = null) } }
    }

    fun getEntityById(id: UUID): Result<NotificationEntity> = notificationRepository.getById(id)

    fun countUnread(): Result<Long> {
        val user = currentUserUseCase.getUser().getOrThrow()
        return notificationRepository.countUnreadByUserId(user.id.internalId)
    }
}
