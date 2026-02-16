package io.github.pedroermarinho.company.controllers

import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.EmployeeInviteEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.employee.EmployeeInviteRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.EmployeeInviteResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.ChangeStatusEmployeeInviteUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.CreateEmployeeInviteUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeInviteUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeInviteMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/api/v1/company/employees/invites")
@Tag(name = "Funcionários", description = "Gerenciamento de funcionários do sistema")
class EmployeeInviteController(
    private val searchEmployeeInviteUseCase: SearchEmployeeInviteUseCase,
    private val createEmployeeInviteUseCase: CreateEmployeeInviteUseCase,
    private val changeStatusEmployeeInviteUseCase: ChangeStatusEmployeeInviteUseCase,
    private val employeeInviteMapper: EmployeeInviteMapper,
) {
    @Operation(summary = "Buscar convites por usuário logado", description = "Buscar convites por usuário logado")
    @GetMapping("/")
    fun getMyInvites(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<EmployeeInviteResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchEmployeeInviteUseCase.getMyInvites(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { employeeInviteMapper.toResponse(it) })
    }

    @Operation(summary = "Buscar convites por restaurante", description = "Buscar convites por restaurante")
    @GetMapping("/company/{companyId}")
    fun getByCompany(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
        @PathVariable companyId: UUID,
    ): ResponseEntity<PageDTO<EmployeeInviteResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchEmployeeInviteUseCase.getByCompanyPublicId(companyId, pageable).getOrThrow()
        return ResponseEntity.ok(result.map { employeeInviteMapper.toResponse(it) })
    }

    @Operation(summary = "Criar convite de funcionário", description = "Criar convite de funcionário")
    @PostMapping
    fun create(
        @Valid @RequestBody form: EmployeeInviteRequest,
        uriBuilder: UriComponentsBuilder,
    ): ResponseEntity<Unit> {
        val result = createEmployeeInviteUseCase.create(form).getOrThrow()
        val uri = uriBuilder.path("/api/v1/employees/invites/{publicId}").buildAndExpand(result.publicId).toUri()
        return ResponseEntity.created(uri).build()
    }

    @Operation(summary = "Buscar convite de funcionário por ID", description = "Buscar convite de funcionário por ID")
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
    ): ResponseEntity<EmployeeInviteResponse> {
        val result = searchEmployeeInviteUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(employeeInviteMapper.toResponse(result))
    }

    @Operation(summary = "Aceitar convite de funcionário", description = "Aceitar convite de funcionário")
    @PatchMapping("/{id}/accept")
    fun accept(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        val result =
            changeStatusEmployeeInviteUseCase
                .changeStatus(id, EmployeeInviteEnum.ACCEPTED)
                .getOrThrow()
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "Recusar convite de funcionário", description = "Recusar convite de funcionário")
    @PatchMapping("/{id}/reject")
    fun reject(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        val result =
            changeStatusEmployeeInviteUseCase
                .changeStatus(id, EmployeeInviteEnum.REJECTED)
                .getOrThrow()
        return ResponseEntity.ok(result)
    }
}
