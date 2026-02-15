package io.github.pedroermarinho.shared.annotations

import org.springframework.stereotype.Service

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Service
annotation class UseCase(
    val readOnly: Boolean = false,
)