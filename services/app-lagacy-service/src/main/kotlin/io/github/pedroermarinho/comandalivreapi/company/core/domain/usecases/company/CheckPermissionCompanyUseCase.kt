package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class CheckPermissionCompanyUseCase(
    private val currentUserUseCase: CurrentUserUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
) {
    fun execute(companyId: UUID): Result<Unit> {
        return runCatching {
            val id = searchCompanyUseCase.getIdById(companyId).getOrThrow()
            return this.execute(id)
        }
    }

    fun execute(companyId: Int): Result<Unit> =
        runCatching {
            val userId = currentUserUseCase.getUserId().getOrThrow()

            val isEmployee =
                searchEmployeeUseCase
                    .isEmployeeOfCompany(
                        userId = userId,
                        companyId = companyId,
                    ).getOrThrow()

            if (!isEmployee) {
                throw BusinessLogicException("O usuário não tem permissão para acessar os recursos desta empresa")
            }
        }
}
