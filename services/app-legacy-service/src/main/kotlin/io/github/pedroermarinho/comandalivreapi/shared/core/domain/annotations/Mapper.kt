package io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations

import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class Mapper
