package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanyEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.CompanyTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.company.CompanySettingsForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.employee.EmployeeForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.company.CompanyCreateRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.CreateEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchRoleTypeUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.CompanyCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateCompanyUseCase(
    private val companyRepository: CompanyRepository,
    private val createEmployeeUseCase: CreateEmployeeUseCase,
    private val searchRoleTypeUseCase: SearchRoleTypeUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val searchTypeCompanyUseCase: SearchTypeCompanyUseCase,
    private val eventPublisher: ApplicationEventPublisher,
    private val searchCompanyUseCase: SearchCompanyUseCase,
) {
    fun create(form: CompanyCreateRequest): Result<EntityId> =
        runCatching {
            val type = searchTypeCompanyUseCase.getByEnum(form.type).getOrThrow()
            val roleEnum =
                when (CompanyTypeEnum.from(type)) {
                    CompanyTypeEnum.RESTAURANT -> RoleTypeEnum.RESTAURANT_OWNER
                    CompanyTypeEnum.CONSTRUCTION_COMPANY -> RoleTypeEnum.CONSTRUCTION_OWNER
                    else -> return Result.failure(BusinessLogicException("Tipo de empresa não suportado"))
                }
            val role = searchRoleTypeUseCase.getByEnum(roleEnum).getOrThrow()
            val user = currentUserUseCase.getUser().getOrThrow()

            val createdCompanyId =
                companyRepository
                    .save(
                        CompanyEntity.createNew(
                            name = form.name,
                            email = form.email,
                            phone = form.phone,
                            cnpj = form.cnpj,
                            description = form.description,
                            companyType = type,
                            isPublic = form.isPublic,
                        ),
                    ).getOrThrow()

            val company = searchCompanyUseCase.getById(createdCompanyId.internalId).getOrThrow()

            createEmployeeUseCase
                .create(
                    EmployeeForm(
                        roleId = role.id.internalId,
                        companyId = createdCompanyId.internalId,
                        userId = user.id.internalId,
                    ),
                ).getOrThrow()

            eventPublisher.publishEvent(
                CompanyCreatedEvent(
                    companyPublicId = createdCompanyId.publicId,
                    companyName = company.name,
                    companyEmail = company.email,
                    ownerId = user.id.internalId,
                    ownerName = user.name,
                    ownerEmail = user.email,
                    ownerSub = user.sub,
                ),
            )

            return Result.success(createdCompanyId)
        }

    fun createSettings(
        companyId: Int,
        form: CompanySettingsForm,
    ): Result<Unit> =
        runCatching {
            val company = companyRepository.getById(companyId).getOrThrow()
            companyRepository
                .save(
                    company.updateSettingsInfor(
                        primaryThemeColor = form.primaryThemeColor,
                        secondaryThemeColor = form.secondaryThemeColor,
                        welcomeMessage = form.welcomeMessage,
                        timezone = form.timezone,
                        openTime = form.openTime,
                        closeTime = form.closeTime,
                        domain = form.domain,
                    ),
                ).map { Unit }
                .getOrThrow()
        }
}
