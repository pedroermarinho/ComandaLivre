package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.services

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class DashboardNotifierService(
    private val messagingTemplate: SimpMessagingTemplate,
) {
    fun notifyDashboardUpdate(
        topic: String,
        payload: Any,
    ) {
        messagingTemplate.convertAndSend("/topic/$topic", payload)
    }
}
