package io.github.pedroermarinho.shared.event

import io.github.pedroermarinho.shared.forms.EventLogForm

data class DomainEventOccurredEvent(
    val form: EventLogForm,
)