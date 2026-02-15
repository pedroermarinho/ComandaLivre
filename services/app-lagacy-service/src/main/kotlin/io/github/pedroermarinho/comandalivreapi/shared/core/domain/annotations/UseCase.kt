package io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations

import org.springframework.stereotype.Service

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Service
annotation class UseCase(
    val readOnly: Boolean = false,
)
