package io.github.pedroermarinho.company.domain.validation

import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class UniqueCompanyNameValidator(
    private val searchCompanyUseCase: SearchCompanyUseCase,
) : ConstraintValidator<UniqueCompanyName, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        if (value == null) {
            return true
        }
        return !searchCompanyUseCase.existsByName(value)
    }
}
