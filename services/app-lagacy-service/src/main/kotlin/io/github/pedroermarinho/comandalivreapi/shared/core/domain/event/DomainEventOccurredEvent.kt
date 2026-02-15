package io.github.pedroermarinho.comandalivreapi.shared.core.domain.event

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.eventlog.EventLogForm

data class DomainEventOccurredEvent(
    val form: EventLogForm,
)
