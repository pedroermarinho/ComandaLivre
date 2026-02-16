package io.github.pedroermarinho.company.controllers

import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.RoleTypeResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchRoleTypeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.RoleTypeMapper
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
@RequestMapping("/api/v1/company/role-types")
@Tag(name = "Role Types", description = "Gerenciamento de tipos de cargos do sistema")
class RoleTypeController(
    private val searchRoleTypeUseCase: SearchRoleTypeUseCase,
    private val roleTypeMapper: RoleTypeMapper,
) {
    @Operation(summary = "Buscar todos os tipos de cargos", description = "Buscar todos os tipos de cargos")
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<RoleTypeResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchRoleTypeUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { roleTypeMapper.toResponse(it) })
    }

    @Operation(summary = "Buscar todos os tipos de cargos", description = "Buscar todos os tipos de cargos")
    @GetMapping("/list")
    fun getAllList(): ResponseEntity<List<RoleTypeResponse>> {
        val result = searchRoleTypeUseCase.getAll().getOrThrow()
        return ResponseEntity.ok(result.map { roleTypeMapper.toResponse(it) })
    }
}
