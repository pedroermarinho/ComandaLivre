package io.github.pedroermarinho.company.domain.validation

import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class UniqueCompanyDomainValidator(
    private val searchCompanyUseCase: SearchCompanyUseCase,
) : ConstraintValidator<UniqueCompanyDomain, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        if (value == null) {
            return true
        }
        return !searchCompanyUseCase.existsByDomain(value)
    }
}
