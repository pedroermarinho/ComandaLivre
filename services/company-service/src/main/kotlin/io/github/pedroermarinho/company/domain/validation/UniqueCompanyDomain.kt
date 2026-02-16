package io.github.pedroermarinho.company.domain.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueCompanyDomainValidator::class])
annotation class UniqueCompanyDomain(
    val message: String = "Já existe uma empresa com este domínio.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
