package io.github.pedroermarinho.user.presenter.controllers

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.response.notification.NotificationResponse
import io.github.pedroermarinho.user.domain.usecases.notification.MarkNotificationAsReadUseCase
import io.github.pedroermarinho.user.domain.usecases.notification.SearchNotificationUseCase
import io.github.pedroermarinho.user.infra.mappers.NotificationMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/shared/notifications")
@Tag(name = "Notificações", description = "Gerenciamento de notificações")
class NotificationController(
    private val searchNotificationUseCase: SearchNotificationUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val notificationMapper: NotificationMapper,
) {
    @Operation(summary = "Buscar notificações do usuário logado")
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<NotificationResponse>> {
        val pageable = PageableDTO(pageNumber = pageNumber, pageSize = pageSize, sort = sort, direction = direction)
        val notifications = searchNotificationUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(notifications.map { notificationMapper.toResponse(it) })
    }

    @Operation(summary = "Contar notificações não lidas do usuário logado")
    @GetMapping("/unread/count")
    fun countUnread(): ResponseEntity<Long> {
        val count = searchNotificationUseCase.countUnread().getOrThrow()
        return ResponseEntity.ok(count)
    }

    @Operation(summary = "Marcar notificação como lida")
    @PutMapping("/{id}/read")
    fun markAsRead(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        markNotificationAsReadUseCase.execute(id).getOrThrow()
        return ResponseEntity.ok().build()
    }
}
