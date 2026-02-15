package io.github.pedroermarinho.comandalivreapi.company.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanyTypeResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchTypeCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyTypeMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/company/company-types")
@Tag(name = "Tipos de Empresas", description = "Gerenciamento de tipos de empresas")
class CompanyTypeController(
    private val searchTypeCompanyUseCase: SearchTypeCompanyUseCase,
    private val companyTypeMapper: CompanyTypeMapper,
) {
    @Operation(
        summary = "Buscar todos os tipos de empresa de forma paginada",
        description = "Buscar todos os tipos de empresa de forma paginada",
    )
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<CompanyTypeResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchTypeCompanyUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { companyTypeMapper.toResponse(it) })
    }

    @Operation(
        summary = "Buscar todos os tipos de empresa",
        description = "Buscar todos os tipos de empresa",
    )
    @GetMapping("/list")
    fun getAllList(): ResponseEntity<List<CompanyTypeResponse>> {
        val result = searchTypeCompanyUseCase.getAll().getOrThrow()
        return ResponseEntity.ok(result.map { companyTypeMapper.toResponse(it) })
    }
}
