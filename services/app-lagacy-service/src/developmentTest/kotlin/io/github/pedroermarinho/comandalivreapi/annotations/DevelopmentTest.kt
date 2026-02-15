package io.github.pedroermarinho.comandalivreapi.annotations

import org.junit.jupiter.api.Tag

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Tag("development")
annotation class DevelopmentTest
