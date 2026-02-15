package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.company.CompanySettingsForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.company.CompanyUpdateRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.DomainName
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.address.AddressForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address.CreateAddressUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address.UpdateAddressUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateCompanyUseCase(
    private val companyRepository: CompanyRepository,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val createAddressUseCase: CreateAddressUseCase,
    private val createCompanyUseCase: CreateCompanyUseCase,
    private val checkPermissionCompanyUseCase: CheckPermissionCompanyUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun update(
        companyId: UUID,
        form: CompanyUpdateRequest,
    ): Result<Unit> =
        runCatching {
            log.info { "Iniciando atualização da empresa com ID: $companyId" }

            checkPermissionCompanyUseCase.execute(companyId).getOrThrow()
            val company = companyRepository.getById(companyId).getOrThrow()

            companyRepository
                .save(
                    company.update(
                        name = form.name,
                        email = form.email,
                        phone = form.phone,
                        cnpj = form.cnpj,
                        description = form.description,
                        isPublic = form.isPublic,
                    ),
                ).getOrThrow()

            return@runCatching
        }.onSuccess {
            log.info { "Empresa com ID: $companyId atualizada com sucesso" }
        }.onFailure { e ->
            log.error(e) { "Falha ao atualizar a empresa com ID: $companyId" }
        }

    fun updateSettings(
        companyId: UUID,
        form: CompanySettingsForm,
    ): Result<Unit> {
        log.info { "Iniciando atualização das configurações da empresa com ID: $companyId" }
        return runCatching {
            checkPermissionCompanyUseCase.execute(companyId).getOrThrow()
            val company = companyRepository.getById(companyId).getOrThrow()
            log.debug { "Verificando disponibilidade do domínio: ${form.domain} para a empresa $companyId" }

            checkDomainAvailability(
                form.domain?.let { DomainName(it) },
                company.settings.domain,
            )

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
                ).getOrThrow()

            return@runCatching
        }.onSuccess {
            log.info { "Configurações da empresa com ID: $companyId atualizadas com sucesso" }
        }.onFailure { e ->
            log.error(e) { "Falha ao atualizar as configurações da empresa com ID: $companyId" }
        }
    }

    fun updateAddress(
        companyId: UUID,
        form: AddressForm,
    ): Result<Unit> {
        log.info { "Iniciando atualização de endereço da empresa com ID: $companyId" }
        return runCatching {
            checkPermissionCompanyUseCase.execute(companyId).getOrThrow()
            val company = companyRepository.getById(companyId).getOrThrow()

            if (company.addressId == null) {
                log.debug { "A empresa $companyId não possui endereço. Criando um novo." }
                val newAddressId = createAddressUseCase.create(form).getOrThrow()
                companyRepository.save(company.updateAddress(newAddressId.internalId)).getOrThrow()
                return@runCatching
            }
            log.debug { "A empresa $companyId já possui endereço. Atualizando o existente." }
            updateAddressUseCase.update(company.addressId.value, form).map { Unit }.getOrThrow()
            return@runCatching
        }.onSuccess {
            log.info { "Endereço da empresa com ID: $companyId atualizado com sucesso" }
        }.onFailure { e ->
            log.error(e) { "Falha ao atualizar o endereço da empresa com ID: $companyId" }
        }
    }

    private fun checkDomainAvailability(
        domain: DomainName?,
        currentDomain: DomainName?,
    ) {
        if (domain == null) {
            return
        }

        if (domain == currentDomain) {
            return
        }

        if (searchCompanyUseCase.existDomain(domain.value)) {
            throw BusinessLogicException("Domínio já está em uso.")
        }
    }
}
