package io.github.pedroermarinho.comandalivreapi.company.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyCountByTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.company.CompanySettingsForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.company.CompanyCreateRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.company.CompanyUpdateRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanyResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.CreateCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.UpdateCompanyImageUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.UpdateCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.RequirePermissions
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FeatureEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.address.AddressForm
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/api/v1/company/companies")
@Tag(name = "Empresas", description = "Gerenciamento de empresas")
class CompanyController(
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val createCompanyUseCase: CreateCompanyUseCase,
    private val updateCompanyUseCase: UpdateCompanyUseCase,
    private val updateCompanyImageUseCase: UpdateCompanyImageUseCase,
    private val companyMapper: CompanyMapper,
) {
    @Operation(summary = "Buscar todos os empresas", description = "Buscar todos os empresas")
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<CompanyResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )

        val result = searchCompanyUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { companyMapper.toResponse(it) })
    }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Buscar a quantidade de empresas ativos no sistema",
        description = "Buscar a quantidade de empresas ativos no sistema",
    )
    @GetMapping("/count")
    fun count(): ResponseEntity<Long> {
        val result = searchCompanyUseCase.count().getOrThrow()
        return ResponseEntity.ok(result)
    }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Contar empresas por tipo",
        description = "Retorna a quantidade de empresas agrupadas por tipo",
    )
    @GetMapping("/count-by-type")
    fun countByType(): ResponseEntity<List<CompanyCountByTypeDTO>> {
        val result = searchCompanyUseCase.countByType().getOrThrow()
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "Buscar restaurante por ID público", description = "Buscar restaurante por ID público")
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
    ): ResponseEntity<CompanyResponse> {
        val result = searchCompanyUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(companyMapper.toResponse(result))
    }

    @Operation(summary = "Buscar restaurante por domínio", description = "Buscar restaurante por domínio")
    @GetMapping("/domain/{domain}")
    fun getByDomain(
        @PathVariable domain: String,
    ): ResponseEntity<CompanyResponse> {
        val result = searchCompanyUseCase.getByDomain(domain).getOrThrow()
        return ResponseEntity.ok(companyMapper.toResponse(result))
    }

    @Operation(summary = "Criar um restaurante", description = "Criar um restaurante")
    @PostMapping
    @Transactional
    fun create(
        @Valid @RequestBody restaurantCreateForm: CompanyCreateRequest,
        uriBuilder: UriComponentsBuilder,
    ): ResponseEntity<Unit> {
        val result = createCompanyUseCase.create(restaurantCreateForm).getOrThrow()
        val uri = uriBuilder.path("/api/v1/companies/{publicId}").buildAndExpand(result.publicId).toUri()
        return ResponseEntity.created(uri).build()
    }

    @Operation(summary = "Atualizar um restaurante", description = "Atualizar um restaurante")
    @PutMapping("/{id}")
    @Transactional
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody form: CompanyUpdateRequest,
    ): ResponseEntity<Unit> {
        updateCompanyUseCase.update(id, form).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Atualizar endereço da empresa", description = "Atualizar endereço da empresa")
    @PutMapping("/{id}/address")
    @Transactional
    fun updateAddress(
        @PathVariable id: UUID,
        @Valid @RequestBody form: AddressForm,
    ): ResponseEntity<Unit> {
        updateCompanyUseCase.updateAddress(id, form).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Atualizar configurações da empresa", description = "Atualizar configurações da empresa")
    @PutMapping("/{id}/settings")
    @Transactional
    fun updateSettings(
        @PathVariable id: UUID,
        @Valid @RequestBody form: CompanySettingsForm,
    ): ResponseEntity<Unit> {
        updateCompanyUseCase.updateSettings(id, form).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Upload do logo de uma empresa",
        description = "Realiza o upload do logo para uma empresa específica.",
    )
    @PatchMapping("/{id}/logo", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProductImage(
        @Parameter(description = "ID público da empresa") @PathVariable id: UUID,
        @Parameter(description = "Arquivo de imagem a ser enviado") @RequestPart("imageFile") imageFile: MultipartFile,
    ): ResponseEntity<Unit> {
        updateCompanyImageUseCase.updateLogo(id, imageFile).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Upload do banner de uma empresa",
        description = "Realiza o upload do banner para uma empresa específica.",
    )
    @PatchMapping("/{id}/banner", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProductBanner(
        @Parameter(description = "ID público da empresa") @PathVariable id: UUID,
        @Parameter(description = "Arquivo de imagem a ser enviado") @RequestPart("imageFile") imageFile: MultipartFile,
    ): ResponseEntity<Unit> {
        updateCompanyImageUseCase.updateBanner(id, imageFile).getOrThrow()
        return ResponseEntity.ok().build()
    }
}
