package io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FeatureEnum

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequirePermissions(
    val all: Array<FeatureEnum> = [],
    val any: Array<FeatureEnum> = [],
    val message: String = "Acesso negado: permissões insuficientes",
)
