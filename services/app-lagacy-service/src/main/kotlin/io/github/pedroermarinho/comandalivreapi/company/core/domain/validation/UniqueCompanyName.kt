package io.github.pedroermarinho.comandalivreapi.company.core.domain.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueCompanyNameValidator::class])
annotation class UniqueCompanyName(
    val message: String = "Já existe uma empresa com este nome.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
